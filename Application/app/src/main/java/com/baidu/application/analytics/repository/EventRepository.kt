package com.baidu.application.analytics.repository

import android.content.Context
import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.model.EventStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 事件仓库
 *
 * 负责事件的本地存储和管理
 */
class EventRepository(context: Context) {

    private val database = EventDatabase.getInstance(context)
    private val eventDao = database.eventDao()

    /**
     * 插入事件
     */
    suspend fun insertEvent(event: EventModel) = withContext(Dispatchers.IO) {
        val entity = EventEntity.fromEventModel(event)
        eventDao.insert(entity)
    }

    /**
     * 批量插入事件
     */
    suspend fun insertEvents(events: List<EventModel>) = withContext(Dispatchers.IO) {
        val entities = events.map { EventEntity.fromEventModel(it) }
        eventDao.insertAll(entities)
    }

    /**
     * 获取待上传事件
     */
    suspend fun getPendingEvents(limit: Int = 50): List<EventModel> = withContext(Dispatchers.IO) {
        eventDao.getPendingEvents(limit).map { it.toEventModel() }
    }

    /**
     * 获取失败事件（可重试）
     */
    suspend fun getFailedEvents(maxRetryCount: Int = 3, limit: Int = 50): List<EventModel> =
        withContext(Dispatchers.IO) {
            eventDao.getFailedEvents(maxRetryCount, limit).map { it.toEventModel() }
        }

    /**
     * 更新事件状态
     */
    suspend fun updateEventStatus(eventId: String, status: EventStatus) = withContext(Dispatchers.IO) {
        eventDao.updateStatus(eventId, status.name, System.currentTimeMillis())
    }

    /**
     * 批量更新事件状态
     */
    suspend fun updateEventsStatus(eventIds: List<String>, status: EventStatus) =
        withContext(Dispatchers.IO) {
            eventDao.updateStatusBatch(eventIds, status.name, System.currentTimeMillis())
        }

    /**
     * 删除事件
     */
    suspend fun deleteEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteById(eventId)
    }

    /**
     * 批量删除事件
     */
    suspend fun deleteEvents(eventIds: List<String>) = withContext(Dispatchers.IO) {
        eventDao.deleteByIds(eventIds)
    }

    /**
     * 删除所有事件
     */
    suspend fun clearAllEvents() = withContext(Dispatchers.IO) {
        eventDao.deleteAll()
    }

    /**
     * 删除过期事件
     */
    suspend fun deleteExpiredEvents(days: Int = 7) = withContext(Dispatchers.IO) {
        val expireTime = System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L
        eventDao.deleteExpiredEvents(expireTime)
    }

    /**
     * 增加重试次数
     */
    suspend fun incrementRetryCount(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.incrementRetryCount(eventId, System.currentTimeMillis())
    }

    /**
     * 获取事件统计信息
     */
    suspend fun getEventStats(): Map<String, Int> = withContext(Dispatchers.IO) {
        mapOf(
            "total" to eventDao.getCount(),
            "pending" to eventDao.getCountByStatus(EventStatus.PENDING.name),
            "uploading" to eventDao.getCountByStatus(EventStatus.UPLOADING.name),
            "success" to eventDao.getCountByStatus(EventStatus.SUCCESS.name),
            "failed" to eventDao.getCountByStatus(EventStatus.FAILED.name)
        )
    }

    /**
     * 关闭数据库
     */
    fun close() {
        database.close()
    }
}
