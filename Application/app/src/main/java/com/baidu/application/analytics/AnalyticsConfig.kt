package com.baidu.application.analytics

import android.content.Context

/**
 * 埋点系统配置类
 *
 * 使用 Builder 模式构建配置
 *
 * @property context 应用上下文
 * @property appKey 应用密钥
 * @property uploadUrl 上传地址
 * @property enableDebug 是否开启调试模式
 * @property enablePrivacy 是否开启隐私保护
 * @property batchSize 批量上传数量
 * @property uploadInterval 上传间隔（毫秒）
 * @property maxCacheSize 最大缓存数量
 * @property enableEncryption 是否开启加密
 * @property enableCompression 是否开启压缩
 * @property wifiOnly 是否仅 WiFi 上传
 * @property retryCount 失败重试次数
 * @property enableAutoTrack 是否开启自动埋点
 * @property enableCrashTrack 是否开启 Crash 追踪
 * @property sessionTimeout 会话超时时间（毫秒）
 */
data class AnalyticsConfig(
    val context: Context,
    val appKey: String,
    val uploadUrl: String,
    val enableDebug: Boolean = false,
    val enablePrivacy: Boolean = true,
    val batchSize: Int = 20,
    val uploadInterval: Long = 30_000L,
    val maxCacheSize: Int = 1000,
    val enableEncryption: Boolean = false,
    val enableCompression: Boolean = true,
    val wifiOnly: Boolean = false,
    val retryCount: Int = 3,
    val enableAutoTrack: Boolean = false,
    val enableCrashTrack: Boolean = true,
    val sessionTimeout: Long = 30 * 60 * 1000L // 30 分钟
) {

    /**
     * Builder 构建器
     */
    class Builder(private val context: Context) {
        private var appKey: String = ""
        private var uploadUrl: String = ""
        private var enableDebug: Boolean = false
        private var enablePrivacy: Boolean = true
        private var batchSize: Int = 20
        private var uploadInterval: Long = 30_000L
        private var maxCacheSize: Int = 1000
        private var enableEncryption: Boolean = false
        private var enableCompression: Boolean = true
        private var wifiOnly: Boolean = false
        private var retryCount: Int = 3
        private var enableAutoTrack: Boolean = false
        private var enableCrashTrack: Boolean = true
        private var sessionTimeout: Long = 30 * 60 * 1000L

        /**
         * 设置应用密钥
         */
        fun setAppKey(appKey: String) = apply {
            this.appKey = appKey
        }

        /**
         * 设置上传地址
         */
        fun setUploadUrl(uploadUrl: String) = apply {
            this.uploadUrl = uploadUrl
        }

        /**
         * 设置是否开启调试模式
         */
        fun setEnableDebug(enable: Boolean) = apply {
            this.enableDebug = enable
        }

        /**
         * 设置是否开启隐私保护
         */
        fun setEnablePrivacy(enable: Boolean) = apply {
            this.enablePrivacy = enable
        }

        /**
         * 设置批量上传数量
         */
        fun setBatchSize(size: Int) = apply {
            this.batchSize = size
        }

        /**
         * 设置上传间隔（毫秒）
         */
        fun setUploadInterval(interval: Long) = apply {
            this.uploadInterval = interval
        }

        /**
         * 设置最大缓存数量
         */
        fun setMaxCacheSize(size: Int) = apply {
            this.maxCacheSize = size
        }

        /**
         * 设置是否开启加密
         */
        fun setEnableEncryption(enable: Boolean) = apply {
            this.enableEncryption = enable
        }

        /**
         * 设置是否开启压缩
         */
        fun setEnableCompression(enable: Boolean) = apply {
            this.enableCompression = enable
        }

        /**
         * 设置是否仅 WiFi 上传
         */
        fun setWifiOnly(wifiOnly: Boolean) = apply {
            this.wifiOnly = wifiOnly
        }

        /**
         * 设置失败重试次数
         */
        fun setRetryCount(count: Int) = apply {
            this.retryCount = count
        }

        /**
         * 设置是否开启自动埋点
         */
        fun setEnableAutoTrack(enable: Boolean) = apply {
            this.enableAutoTrack = enable
        }

        /**
         * 设置是否开启 Crash 追踪
         */
        fun setEnableCrashTrack(enable: Boolean) = apply {
            this.enableCrashTrack = enable
        }

        /**
         * 设置会话超时时间（毫秒）
         */
        fun setSessionTimeout(timeout: Long) = apply {
            this.sessionTimeout = timeout
        }

        /**
         * 构建配置对象
         */
        fun build(): AnalyticsConfig {
            require(appKey.isNotEmpty()) { "appKey cannot be empty" }
            require(uploadUrl.isNotEmpty()) { "uploadUrl cannot be empty" }

            return AnalyticsConfig(
                context = context.applicationContext,
                appKey = appKey,
                uploadUrl = uploadUrl,
                enableDebug = enableDebug,
                enablePrivacy = enablePrivacy,
                batchSize = batchSize,
                uploadInterval = uploadInterval,
                maxCacheSize = maxCacheSize,
                enableEncryption = enableEncryption,
                enableCompression = enableCompression,
                wifiOnly = wifiOnly,
                retryCount = retryCount,
                enableAutoTrack = enableAutoTrack,
                enableCrashTrack = enableCrashTrack,
                sessionTimeout = sessionTimeout
            )
        }
    }
}
