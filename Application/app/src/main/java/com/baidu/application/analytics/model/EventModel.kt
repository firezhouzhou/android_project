package com.baidu.application.analytics.model

import kotlinx.serialization.Serializable

/**
 * 埋点事件模型
 *
 * @property eventId 事件唯一 ID
 * @property eventName 事件名称
 * @property timestamp 时间戳（毫秒）
 * @property traceId 追踪 ID（用于链路追踪）
 * @property sessionId 会话 ID
 * @property userId 用户 ID（可选）
 * @property commonParams 公共参数（设备信息、应用信息等）
 * @property businessParams 业务参数（业务自定义）
 * @property status 事件状态
 * @property retryCount 重试次数
 * @property createTime 创建时间
 * @property updateTime 更新时间
 */
@Serializable
data class EventModel(
    val eventId: String,
    val eventName: String,
    val timestamp: Long,
    val traceId: String,
    val sessionId: String,
    val userId: String? = null,
    val commonParams: Map<String, String> = emptyMap(),
    val businessParams: Map<String, String> = emptyMap(),
    val status: EventStatus = EventStatus.PENDING,
    val retryCount: Int = 0,
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis()
) {
    /**
     * 合并所有参数
     */
    fun getAllParams(): Map<String, String> {
        return commonParams + businessParams
    }

    /**
     * 转换为 JSON 字符串
     */
    fun toJson(): String {
        return kotlinx.serialization.json.Json.encodeToString(serializer(), this)
    }

    companion object {
        /**
         * 从 JSON 字符串解析
         */
        fun fromJson(json: String): EventModel {
            return kotlinx.serialization.json.Json.decodeFromString(serializer(), json)
        }
    }
}

/**
 * 事件状态枚举
 */
@Serializable
enum class EventStatus {
    /** 待上传 */
    PENDING,

    /** 上传中 */
    UPLOADING,

    /** 上传成功 */
    SUCCESS,

    /** 上传失败 */
    FAILED
}
