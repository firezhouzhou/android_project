package com.baidu.application.analytics.pipeline

import com.baidu.application.analytics.model.EventModel

/**
 * Pipeline 处理器接口
 *
 * 所有 Pipeline 处理器都需要实现此接口
 */
interface IPipelineProcessor {

    /**
     * 处理事件
     *
     * @param event 输入事件
     * @return 处理后的事件，如果返回 null 则表示事件被过滤
     */
    fun process(event: EventModel): EventModel?
}
