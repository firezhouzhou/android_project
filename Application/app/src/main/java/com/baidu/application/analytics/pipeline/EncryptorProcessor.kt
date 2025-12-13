package com.baidu.application.analytics.pipeline

import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.util.CryptoUtil

/**
 * 加密处理器
 *
 * 对事件数据进行加密（仅在上传时加密，本地存储不加密）
 */
class EncryptorProcessor(
    private val encryptionKey: String
) : IPipelineProcessor {

    override fun process(event: EventModel): EventModel {
        // 注意：加密操作通常在上传时进行，这里仅做标记
        // 实际加密在 Uploader 中执行
        return event
    }
}
