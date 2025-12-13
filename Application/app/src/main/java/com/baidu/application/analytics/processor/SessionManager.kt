package com.baidu.application.analytics.processor

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

/**
 * 会话管理器
 *
 * 负责管理 sessionId 和 traceId
 */
class SessionManager(
    context: Context,
    private val sessionTimeout: Long
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "analytics_session",
        Context.MODE_PRIVATE
    )

    private var currentSessionId: String? = null
    private var currentTraceId: String? = null
    private var lastActiveTime: Long = 0L

    init {
        // 恢复上次的会话信息
        currentSessionId = prefs.getString(KEY_SESSION_ID, null)
        currentTraceId = prefs.getString(KEY_TRACE_ID, null)
        lastActiveTime = prefs.getLong(KEY_LAST_ACTIVE_TIME, 0L)

        // 检查会话是否过期
        if (isSessionExpired()) {
            startNewSession()
        }
    }

    /**
     * 获取当前会话 ID
     */
    fun getSessionId(): String {
        if (isSessionExpired()) {
            startNewSession()
        }
        updateLastActiveTime()
        return currentSessionId!!
    }

    /**
     * 获取当前追踪 ID
     */
    fun getTraceId(): String {
        if (currentTraceId == null) {
            currentTraceId = UUID.randomUUID().toString()
            saveTraceId()
        }
        return currentTraceId!!
    }

    /**
     * 开始新会话
     */
    private fun startNewSession() {
        currentSessionId = UUID.randomUUID().toString()
        currentTraceId = UUID.randomUUID().toString()
        lastActiveTime = System.currentTimeMillis()

        saveSessionId()
        saveTraceId()
        saveLastActiveTime()
    }

    /**
     * 检查会话是否过期
     */
    private fun isSessionExpired(): Boolean {
        if (currentSessionId == null) return true
        val now = System.currentTimeMillis()
        return (now - lastActiveTime) > sessionTimeout
    }

    /**
     * 更新最后活跃时间
     */
    private fun updateLastActiveTime() {
        lastActiveTime = System.currentTimeMillis()
        saveLastActiveTime()
    }

    /**
     * 清除会话信息
     */
    fun clearSession() {
        currentSessionId = null
        currentTraceId = null
        lastActiveTime = 0L
        prefs.edit().clear().apply()
    }

    private fun saveSessionId() {
        prefs.edit().putString(KEY_SESSION_ID, currentSessionId).apply()
    }

    private fun saveTraceId() {
        prefs.edit().putString(KEY_TRACE_ID, currentTraceId).apply()
    }

    private fun saveLastActiveTime() {
        prefs.edit().putLong(KEY_LAST_ACTIVE_TIME, lastActiveTime).apply()
    }

    companion object {
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_TRACE_ID = "trace_id"
        private const val KEY_LAST_ACTIVE_TIME = "last_active_time"
    }
}
