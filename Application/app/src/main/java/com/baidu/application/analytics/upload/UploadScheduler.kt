package com.baidu.application.analytics.upload

import android.content.Context
import androidx.work.*
import com.baidu.application.analytics.AnalyticsConfig
import com.baidu.application.analytics.model.EventStatus
import com.baidu.application.analytics.repository.EventRepository
import com.baidu.application.analytics.util.NetworkUtil
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * 上传调度器
 *
 * 负责管理事件上传时机和策略
 */
class UploadScheduler(
    private val config: AnalyticsConfig,
    private val repository: EventRepository
) {

    private val context: Context = config.context
    private var uploader: IUploader = HttpUploader(config)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var uploadJob: Job? = null
    private var isRunning = false

    /**
     * 启动调度器
     */
    fun start() {
        if (isRunning) return
        isRunning = true

        // 启动定时上传任务
        schedulePeriodicUpload()

        // 清理过期事件
        scope.launch {
            repository.deleteExpiredEvents(7)
        }
    }

    /**
     * 停止调度器
     */
    fun stop() {
        isRunning = false
        uploadJob?.cancel()
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /**
     * 设置自定义上传器
     */
    fun setUploader(uploader: IUploader) {
        this.uploader = uploader
    }

    /**
     * 检查并调度上传
     */
    fun checkAndScheduleUpload() {
        scope.launch {
            val stats = repository.getEventStats()
            val pendingCount = stats["pending"] ?: 0

            // 如果待上传事件数量达到阈值，立即上传
            if (pendingCount >= config.batchSize) {
                performUpload()
            }
        }
    }

    /**
     * 强制立即上传
     */
    fun forceUpload() {
        scope.launch {
            performUpload()
        }
    }

    /**
     * 调度定期上传任务
     */
    private fun schedulePeriodicUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (config.wifiOnly) NetworkType.UNMETERED else NetworkType.CONNECTED
            )
            .build()

        val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(
            config.uploadInterval,
            TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadRequest
        )
    }

    /**
     * 执行上传
     */
    suspend fun performUpload() {
        if (uploadJob?.isActive == true) {
            return // 已有上传任务在执行
        }

        uploadJob = scope.launch {
            try {
                // 检查网络状态
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    logDebug("Network not available, skip upload")
                    return@launch
                }

                // 如果配置了仅 WiFi 上传，检查是否为 WiFi
                if (config.wifiOnly && !NetworkUtil.isWifiConnected(context)) {
                    logDebug("WiFi only mode, but not connected to WiFi")
                    return@launch
                }

                // 获取待上传事件
                val pendingEvents = repository.getPendingEvents(config.batchSize)
                if (pendingEvents.isEmpty()) {
                    logDebug("No pending events to upload")
                    return@launch
                }

                logDebug("Start uploading ${pendingEvents.size} events")

                // 更新状态为上传中
                val eventIds = pendingEvents.map { it.eventId }
                repository.updateEventsStatus(eventIds, EventStatus.UPLOADING)

                // 执行上传
                val result = uploader.upload(pendingEvents)

                if (result.isSuccess) {
                    // 上传成功，删除事件
                    repository.deleteEvents(eventIds)
                    logDebug("Upload success, ${pendingEvents.size} events deleted")
                } else {
                    // 上传失败，更新状态并增加重试次数
                    handleUploadFailure(pendingEvents, result.exceptionOrNull())
                }

            } catch (e: Exception) {
                logError("Upload error", e)
            }
        }
    }

    /**
     * 处理上传失败
     */
    private suspend fun handleUploadFailure(events: List<com.baidu.application.analytics.model.EventModel>, error: Throwable?) {
        logError("Upload failed: ${error?.message}")

        for (event in events) {
            if (event.retryCount < config.retryCount) {
                // 增加重试次数
                repository.incrementRetryCount(event.eventId)
                repository.updateEventStatus(event.eventId, EventStatus.PENDING)
            } else {
                // 超过最大重试次数，标记为失败
                repository.updateEventStatus(event.eventId, EventStatus.FAILED)
            }
        }
    }

    private fun logDebug(message: String) {
        if (config.enableDebug) {
            android.util.Log.d("UploadScheduler", message)
        }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        if (config.enableDebug) {
            android.util.Log.e("UploadScheduler", message, throwable)
        }
    }

    companion object {
        private const val WORK_NAME = "analytics_upload_work"
    }
}
