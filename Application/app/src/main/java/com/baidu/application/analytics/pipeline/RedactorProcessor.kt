package com.baidu.application.analytics.pipeline

import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.privacy.PIIRedactor

/**
 * 脱敏处理器
 *
 * 对敏感信息进行脱敏处理
 */
class RedactorProcessor : IPipelineProcessor {

    private val piiRedactor = PIIRedactor()

    override fun process(event: EventModel): EventModel {
        // 脱敏业务参数
        val redactedBusinessParams = event.businessParams.mapValues { (key, value) ->
            when {
                key.contains("phone", ignoreCase = true) -> piiRedactor.redactPhone(value)
                key.contains("id_card", ignoreCase = true) -> piiRedactor.redactIdCard(value)
                key.contains("email", ignoreCase = true) -> piiRedactor.redactEmail(value)
                key.contains("address", ignoreCase = true) -> piiRedactor.redactAddress(value)
                key.contains("name", ignoreCase = true) -> piiRedactor.redactName(value)
                else -> value
            }
        }

        return event.copy(businessParams = redactedBusinessParams)
    }
}
