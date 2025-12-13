package com.baidu.application.analytics.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.model.EventStatus
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 事件数据库实体
 */
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val eventId: String,
    val eventName: String,
    val timestamp: Long,
    val traceId: String,
    val sessionId: String,
    val userId: String?,
    val commonParamsJson: String,
    val businessParamsJson: String,
    val status: String,
    val retryCount: Int,
    val createTime: Long,
    val updateTime: Long
) {
    /**
     * 转换为 EventModel
     */
    fun toEventModel(): EventModel {
        return EventModel(
            eventId = eventId,
            eventName = eventName,
            timestamp = timestamp,
            traceId = traceId,
            sessionId = sessionId,
            userId = userId,
            commonParams = Json.decodeFromString(commonParamsJson),
            businessParams = Json.decodeFromString(businessParamsJson),
            status = EventStatus.valueOf(status),
            retryCount = retryCount,
            createTime = createTime,
            updateTime = updateTime
        )
    }

    companion object {
        /**
         * 从 EventModel 创建
         */
        fun fromEventModel(event: EventModel): EventEntity {
            return EventEntity(
                eventId = event.eventId,
                eventName = event.eventName,
                timestamp = event.timestamp,
                traceId = event.traceId,
                sessionId = event.sessionId,
                userId = event.userId,
                commonParamsJson = Json.encodeToString(event.commonParams),
                businessParamsJson = Json.encodeToString(event.businessParams),
                status = event.status.name,
                retryCount = event.retryCount,
                createTime = event.createTime,
                updateTime = event.updateTime
            )
        }
    }
}
