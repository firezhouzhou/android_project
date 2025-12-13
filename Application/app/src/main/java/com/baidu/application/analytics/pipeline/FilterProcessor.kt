package com.baidu.application.analytics.pipeline

import com.baidu.application.analytics.AnalyticsConfig
import com.baidu.application.analytics.model.EventModel

/**
 * 过滤器处理器
 *
 * 过滤不需要上报的事件
 */
class FilterProcessor(
    private val config: AnalyticsConfig
) : IPipelineProcessor {

    // 黑名单事件（不上报）
    private val blacklist = setOf(
        "debug_event",
        "test_event"
    )

    override fun process(event: EventModel): EventModel? {
        // 调试模式下不过滤
        if (config.enableDebug) {
            return event
        }

        // 检查黑名单
        if (event.eventName in blacklist) {
            return null
        }

        // 检查事件名称是否合法
        if (event.eventName.isBlank()) {
            return null
        }

        return event
    }
}
