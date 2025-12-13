package com.baidu.application.analytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Analytics Java 适配器
 * <p>
 * 为 Java 代码提供更友好的 API
 */
public class AnalyticsJavaAdapter {

    /**
     * 初始化埋点系统
     *
     * @param config 配置对象
     */
    public static void init(AnalyticsConfig config) {
        Analytics.init(config);
    }

    /**
     * 记录事件（无参数）
     *
     * @param eventName 事件名称
     */
    public static void logEvent(String eventName) {
        Map<String, String> emptyMap = new HashMap<>();
        Analytics.logEvent(eventName, emptyMap);
    }

    /**
     * 记录事件（带参数）
     *
     * @param eventName 事件名称
     * @param params    事件参数
     */
    public static void logEvent(String eventName, Map<String, String> params) {
        Analytics.logEvent(eventName, params);
    }

    /**
     * 设置用户 ID
     *
     * @param userId 用户 ID
     */
    public static void setUserId(String userId) {
        Analytics.setUserId(userId);
    }

    /**
     * 设置用户属性
     *
     * @param key   属性键
     * @param value 属性值
     */
    public static void setUserProperty(String key, String value) {
        Analytics.setUserProperty(key, value);
    }

    /**
     * 批量设置用户属性
     *
     * @param properties 属性 Map
     */
    public static void setUserProperties(Map<String, String> properties) {
        Analytics.setUserProperties(properties);
    }

    /**
     * 设置隐私开关
     *
     * @param enabled 是否开启隐私保护
     */
    public static void setPrivacyEnabled(boolean enabled) {
        Analytics.setPrivacyEnabled(enabled);
    }

    /**
     * 立即上传所有待上传事件
     */
    public static void flush() {
        Analytics.flush();
    }

    /**
     * 清空本地缓存
     */
    public static void clearCache() {
        Analytics.clearCache();
    }

    /**
     * 清空所有数据
     */
    public static void clearAllData() {
        Analytics.clearAllData();
    }

    /**
     * 获取当前会话 ID
     */
    public static String getSessionId() {
        return Analytics.getSessionId();
    }

    /**
     * 获取当前追踪 ID
     */
    public static String getTraceId() {
        return Analytics.getTraceId();
    }
}
