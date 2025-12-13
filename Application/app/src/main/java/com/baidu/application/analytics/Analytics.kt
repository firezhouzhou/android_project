package com.baidu.application.analytics

import android.content.Context
import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.pipeline.*
import com.baidu.application.analytics.processor.EventProcessor
import com.baidu.application.analytics.processor.SessionManager
import com.baidu.application.analytics.repository.EventRepository
import com.baidu.application.analytics.upload.IUploader
import com.baidu.application.analytics.upload.UploadScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 埋点系统统一入口
 *
 * 单例模式，提供全局埋点 API
 *
 * 使用示例：
 * ```kotlin
 * // 初始化
 * val config = AnalyticsConfig.Builder(context)
 *     .setAppKey("your_app_key")
 *     .setUploadUrl("https://api.example.com/analytics")
 *     .build()
 * Analytics.init(config)
 *
 * // 记录事件
 * Analytics.logEvent("button_click", mapOf("button_id" to "submit"))
 *
 * // 设置用户 ID
 * Analytics.setUserId("user_123")
 *
 * // 设置用户属性
 * Analytics.setUserProperty("vip_level", "gold")
 * ```
 */
object Analytics {

    private var config: AnalyticsConfig? = null
    private var eventProcessor: EventProcessor? = null
    private var eventRepository: EventRepository? = null
    private var uploadScheduler: UploadScheduler? = null
    private var sessionManager: SessionManager? = null
    private val pipelineProcessors = mutableListOf<IPipelineProcessor>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var isInitialized = false
    private var privacyEnabled = true
    private var userId: String? = null
    private val userProperties = mutableMapOf<String, String>()

    /**
     * 初始化埋点系统
     *
     * @param config 配置对象
     */
    @JvmStatic
    fun init(config: AnalyticsConfig) {
        if (isInitialized) {
            logDebug("Analytics already initialized")
            return
        }

        this.config = config
        this.privacyEnabled = config.enablePrivacy

        // 初始化会话管理器
        sessionManager = SessionManager(config.context, config.sessionTimeout)

        // 初始化事件处理器
        eventProcessor = EventProcessor(config.context, sessionManager!!)

        // 初始化事件仓库
        eventRepository = EventRepository(config.context)

        // 初始化上传调度器
        uploadScheduler = UploadScheduler(config, eventRepository!!)

        // 初始化 Pipeline
        initPipeline()

        // 启动上传调度器
        uploadScheduler?.start()

        isInitialized = true
        logDebug("Analytics initialized successfully")
    }

    /**
     * 初始化处理链
     */
    private fun initPipeline() {
        val config = this.config ?: return

        // 添加过滤器
        pipelineProcessors.add(FilterProcessor(config))

        // 添加脱敏器
        pipelineProcessors.add(RedactorProcessor())

        // 添加压缩器
        if (config.enableCompression) {
            pipelineProcessors.add(CompressorProcessor())
        }

        // 添加加密器
        if (config.enableEncryption) {
            pipelineProcessors.add(EncryptorProcessor(config.appKey))
        }
    }

    /**
     * 记录事件
     *
     * @param eventName 事件名称
     * @param params 事件参数
     */
    @JvmStatic
    @JvmOverloads
    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        if (!checkInitialized()) return
        if (!privacyEnabled) {
            logDebug("Privacy disabled, event ignored: $eventName")
            return
        }

        scope.launch {
            try {
                // 1. 处理事件参数
                var event = eventProcessor?.processEvent(
                    eventName = eventName,
                    businessParams = params,
                    userId = userId,
                    userProperties = userProperties
                ) ?: return@launch

                // 2. 执行 Pipeline 处理
                for (processor in pipelineProcessors) {
                    event = processor.process(event) ?: return@launch
                }

                // 3. 存储到本地队列
                eventRepository?.insertEvent(event)

                logDebug("Event logged: $eventName")

                // 4. 触发上传检查
                uploadScheduler?.checkAndScheduleUpload()

            } catch (e: Exception) {
                logError("Failed to log event: $eventName", e)
            }
        }
    }

    /**
     * 设置用户 ID
     *
     * @param userId 用户 ID
     */
    @JvmStatic
    fun setUserId(userId: String?) {
        if (!checkInitialized()) return
        this.userId = userId
        logDebug("User ID set: $userId")
    }

    /**
     * 设置用户属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    @JvmStatic
    fun setUserProperty(key: String, value: String) {
        if (!checkInitialized()) return
        userProperties[key] = value
        logDebug("User property set: $key = $value")
    }

    /**
     * 批量设置用户属性
     *
     * @param properties 属性 Map
     */
    @JvmStatic
    fun setUserProperties(properties: Map<String, String>) {
        if (!checkInitialized()) return
        userProperties.putAll(properties)
        logDebug("User properties set: ${properties.size} items")
    }

    /**
     * 设置隐私开关
     *
     * @param enabled 是否开启隐私保护
     */
    @JvmStatic
    fun setPrivacyEnabled(enabled: Boolean) {
        if (!checkInitialized()) return

        this.privacyEnabled = enabled

        if (!enabled) {
            // 关闭隐私保护时，清空所有数据
            clearAllData()
            logDebug("Privacy disabled, all data cleared")
        } else {
            logDebug("Privacy enabled")
        }
    }

    /**
     * 立即上传所有待上传事件
     */
    @JvmStatic
    fun flush() {
        if (!checkInitialized()) return
        uploadScheduler?.forceUpload()
        logDebug("Flush triggered")
    }

    /**
     * 清空本地缓存
     */
    @JvmStatic
    fun clearCache() {
        if (!checkInitialized()) return
        scope.launch {
            eventRepository?.clearAllEvents()
            logDebug("Cache cleared")
        }
    }

    /**
     * 清空所有数据（包括用户信息）
     */
    @JvmStatic
    fun clearAllData() {
        if (!checkInitialized()) return
        scope.launch {
            eventRepository?.clearAllEvents()
            userId = null
            userProperties.clear()
            sessionManager?.clearSession()
            logDebug("All data cleared")
        }
    }

    /**
     * 添加自定义 Pipeline 处理器
     *
     * @param processor 处理器
     */
    @JvmStatic
    fun addPipelineProcessor(processor: IPipelineProcessor) {
        if (!checkInitialized()) return
        pipelineProcessors.add(processor)
        logDebug("Pipeline processor added: ${processor::class.simpleName}")
    }

    /**
     * 设置自定义上传器
     *
     * @param uploader 上传器
     */
    @JvmStatic
    fun setUploader(uploader: IUploader) {
        if (!checkInitialized()) return
        uploadScheduler?.setUploader(uploader)
        logDebug("Custom uploader set")
    }

    /**
     * 获取当前会话 ID
     */
    @JvmStatic
    fun getSessionId(): String? {
        return sessionManager?.getSessionId()
    }

    /**
     * 获取当前追踪 ID
     */
    @JvmStatic
    fun getTraceId(): String? {
        return sessionManager?.getTraceId()
    }

    /**
     * 获取事件统计信息
     */
    @JvmStatic
    suspend fun getEventStats(): Map<String, Int> {
        if (!checkInitialized()) return emptyMap()
        return eventRepository?.getEventStats() ?: emptyMap()
    }

    /**
     * 检查是否已初始化
     */
    private fun checkInitialized(): Boolean {
        if (!isInitialized) {
            logError("Analytics not initialized, please call Analytics.init() first")
            return false
        }
        return true
    }

    /**
     * 调试日志
     */
    private fun logDebug(message: String) {
        if (config?.enableDebug == true) {
            android.util.Log.d("Analytics", message)
        }
    }

    /**
     * 错误日志
     */
    private fun logError(message: String, throwable: Throwable? = null) {
        if (config?.enableDebug == true) {
            android.util.Log.e("Analytics", message, throwable)
        }
    }

    /**
     * 销毁（用于测试）
     */
    @JvmStatic
    internal fun destroy() {
        uploadScheduler?.stop()
        eventRepository?.close()
        isInitialized = false
        config = null
        eventProcessor = null
        eventRepository = null
        uploadScheduler = null
        sessionManager = null
        pipelineProcessors.clear()
        userId = null
        userProperties.clear()
    }
}
