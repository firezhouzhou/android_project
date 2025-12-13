package com.baidu.application.analytics.bridge

import android.webkit.JavascriptInterface
import com.baidu.application.analytics.Analytics
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * JSBridge 处理器
 *
 * 用于 H5 与 Android 埋点互通
 *
 * 使用方式：
 * ```kotlin
 * webView.addJavascriptInterface(JSBridgeHandler(), "AndroidAnalytics")
 * ```
 *
 * H5 调用示例：
 * ```javascript
 * window.AndroidAnalytics.logEvent("h5_button_click", JSON.stringify({
 *     button_id: "submit",
 *     page: "login"
 * }));
 * ```
 */
class JSBridgeHandler {

    /**
     * 记录事件
     *
     * @param eventName 事件名称
     * @param paramsJson 事件参数（JSON 字符串）
     */
    @JavascriptInterface
    fun logEvent(eventName: String, paramsJson: String) {
        try {
            val params = parseJsonToMap(paramsJson)
            Analytics.logEvent(eventName, params)
        } catch (e: Exception) {
            android.util.Log.e("JSBridgeHandler", "Failed to log event: $eventName", e)
        }
    }

    /**
     * 设置用户 ID
     *
     * @param userId 用户 ID
     */
    @JavascriptInterface
    fun setUserId(userId: String) {
        Analytics.setUserId(userId)
    }

    /**
     * 设置用户属性
     *
     * @param key 属性键
     * @param value 属性值
     */
    @JavascriptInterface
    fun setUserProperty(key: String, value: String) {
        Analytics.setUserProperty(key, value)
    }

    /**
     * 立即上传
     */
    @JavascriptInterface
    fun flush() {
        Analytics.flush()
    }

    /**
     * 获取会话 ID
     */
    @JavascriptInterface
    fun getSessionId(): String? {
        return Analytics.getSessionId()
    }

    /**
     * 获取追踪 ID
     */
    @JavascriptInterface
    fun getTraceId(): String? {
        return Analytics.getTraceId()
    }

    /**
     * 解析 JSON 字符串为 Map
     */
    private fun parseJsonToMap(jsonString: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val jsonObject = JSONObject(jsonString)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = jsonObject.getString(key)
                map[key] = value
            }
        } catch (e: Exception) {
            android.util.Log.e("JSBridgeHandler", "Failed to parse JSON", e)
        }
        return map
    }
}
