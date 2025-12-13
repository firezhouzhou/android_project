package com.baidu.application.analytics.pipeline

import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.util.CompressionUtil

/**
 * 压缩处理器
 *
 * 对事件数据进行压缩（仅在上传时压缩，本地存储不压缩）
 */
class CompressorProcessor : IPipelineProcessor {

    override fun process(event: EventModel): EventModel {
        // 注意：压缩操作通常在上传时进行，这里仅做标记
        // 实际压缩在 Uploader 中执行
        return event
    }
}
