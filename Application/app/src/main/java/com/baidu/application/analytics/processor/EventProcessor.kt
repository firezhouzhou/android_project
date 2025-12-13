package com.baidu.application.analytics.processor

import android.content.Context
import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.util.DeviceInfoUtil
import java.util.UUID

/**
 * 事件处理器
 *
 * 负责事件参数合并、设备信息添加、traceId/sessionId 生成等
 */
class EventProcessor(
    private val context: Context,
    private val sessionManager: SessionManager
) {

    /**
     * 处理事件
     *
     * @param eventName 事件名称
     * @param businessParams 业务参数
     * @param userId 用户 ID
     * @param userProperties 用户属性
     * @return 处理后的事件模型
     */
    fun processEvent(
        eventName: String,
        businessParams: Map<String, String>,
        userId: String?,
        userProperties: Map<String, String>
    ): EventModel {
        // 生成事件 ID
        val eventId = UUID.randomUUID().toString()

        // 获取当前时间戳
        val timestamp = System.currentTimeMillis()

        // 获取 traceId 和 sessionId
        val traceId = sessionManager.getTraceId()
        val sessionId = sessionManager.getSessionId()

        // 合并公共参数
        val commonParams = buildCommonParams(userId, userProperties)

        return EventModel(
            eventId = eventId,
            eventName = eventName,
            timestamp = timestamp,
            traceId = traceId,
            sessionId = sessionId,
            userId = userId,
            commonParams = commonParams,
            businessParams = businessParams
        )
    }

    /**
     * 构建公共参数
     */
    private fun buildCommonParams(
        userId: String?,
        userProperties: Map<String, String>
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()

        // 设备信息
        params["device_id"] = DeviceInfoUtil.getDeviceId(context)
        params["device_model"] = DeviceInfoUtil.getDeviceModel()
        params["device_brand"] = DeviceInfoUtil.getDeviceBrand()
        params["os_version"] = DeviceInfoUtil.getOsVersion()
        params["screen_width"] = DeviceInfoUtil.getScreenWidth(context).toString()
        params["screen_height"] = DeviceInfoUtil.getScreenHeight(context).toString()

        // 应用信息
        params["app_version"] = DeviceInfoUtil.getAppVersion(context)
        params["app_version_code"] = DeviceInfoUtil.getAppVersionCode(context).toString()
        params["package_name"] = context.packageName

        // 网络信息
        params["network_type"] = DeviceInfoUtil.getNetworkType(context)

        // 用户信息
        userId?.let { params["user_id"] = it }

        // 用户属性
        params.putAll(userProperties)

        return params
    }
}
