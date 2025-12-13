package com.baidu.application.analytics.upload

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.baidu.application.analytics.Analytics

/**
 * 上传 Worker
 *
 * 由 WorkManager 调度执行的后台上传任务
 */
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 触发上传
            Analytics.flush()

            Result.success()
        } catch (e: Exception) {
            // 失败后重试
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
