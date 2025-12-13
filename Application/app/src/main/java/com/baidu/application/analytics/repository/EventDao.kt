package com.baidu.application.analytics.repository

import androidx.room.*

/**
 * 事件数据访问对象
 */
@Dao
interface EventDao {

    /**
     * 插入事件
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    /**
     * 批量插入事件
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>): List<Long>

    /**
     * 更新事件
     */
    @Update
    suspend fun update(event: EventEntity): Int

    /**
     * 删除事件
     */
    @Delete
    suspend fun delete(event: EventEntity): Int

    /**
     * 根据 ID 删除事件
     */
    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deleteById(eventId: String): Int

    /**
     * 批量删除事件
     */
    @Query("DELETE FROM events WHERE eventId IN (:eventIds)")
    suspend fun deleteByIds(eventIds: List<String>): Int

    /**
     * 删除所有事件
     */
    @Query("DELETE FROM events")
    suspend fun deleteAll(): Int

    /**
     * 根据 ID 查询事件
     */
    @Query("SELECT * FROM events WHERE eventId = :eventId")
    suspend fun getById(eventId: String): EventEntity?

    /**
     * 查询所有待上传事件
     */
    @Query("SELECT * FROM events WHERE status = 'PENDING' ORDER BY createTime ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int): List<EventEntity>

    /**
     * 查询所有失败事件
     */
    @Query("SELECT * FROM events WHERE status = 'FAILED' AND retryCount < :maxRetryCount ORDER BY createTime ASC LIMIT :limit")
    suspend fun getFailedEvents(maxRetryCount: Int, limit: Int): List<EventEntity>

    /**
     * 查询事件总数
     */
    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int

    /**
     * 根据状态查询事件数量
     */
    @Query("SELECT COUNT(*) FROM events WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    /**
     * 删除过期事件（超过指定天数）
     */
    @Query("DELETE FROM events WHERE createTime < :expireTime")
    suspend fun deleteExpiredEvents(expireTime: Long): Int

    /**
     * 更新事件状态
     */
    @Query("UPDATE events SET status = :status, updateTime = :updateTime WHERE eventId = :eventId")
    suspend fun updateStatus(eventId: String, status: String, updateTime: Long): Int

    /**
     * 批量更新事件状态
     */
    @Query("UPDATE events SET status = :status, updateTime = :updateTime WHERE eventId IN (:eventIds)")
    suspend fun updateStatusBatch(eventIds: List<String>, status: String, updateTime: Long): Int

    /**
     * 增加重试次数
     */
    @Query("UPDATE events SET retryCount = retryCount + 1, updateTime = :updateTime WHERE eventId = :eventId")
    suspend fun incrementRetryCount(eventId: String, updateTime: Long): Int
}
