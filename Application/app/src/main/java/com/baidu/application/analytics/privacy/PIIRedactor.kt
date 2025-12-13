package com.baidu.application.analytics.privacy

/**
 * PII（个人身份信息）脱敏器
 */
class PIIRedactor {

    /**
     * 脱敏手机号
     * 示例：13812345678 -> 138****5678
     */
    fun redactPhone(phone: String): String {
        if (phone.length != 11) return phone
        return phone.replaceRange(3, 7, "****")
    }

    /**
     * 脱敏身份证号
     * 示例：110101199001011234 -> 110101********1234
     */
    fun redactIdCard(idCard: String): String {
        if (idCard.length < 10) return idCard
        val start = 6
        val end = idCard.length - 4
        return idCard.replaceRange(start, end, "*".repeat(end - start))
    }

    /**
     * 脱敏邮箱
     * 示例：abc@gmail.com -> a***@gmail.com
     */
    fun redactEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email

        val username = parts[0]
        val domain = parts[1]

        val redactedUsername = if (username.length <= 3) {
            username[0] + "***"
        } else {
            username[0] + "***" + username.last()
        }

        return "$redactedUsername@$domain"
    }

    /**
     * 脱敏地址（仅保留省市）
     * 示例：北京市朝阳区建国路1号 -> 北京市朝阳区***
     */
    fun redactAddress(address: String): String {
        // 简单实现：保留前 10 个字符
        return if (address.length > 10) {
            address.substring(0, 10) + "***"
        } else {
            address
        }
    }

    /**
     * 脱敏姓名（仅保留姓氏）
     * 示例：张三 -> 张*
     */
    fun redactName(name: String): String {
        if (name.isEmpty()) return name
        return name[0] + "*".repeat(name.length - 1)
    }

    /**
     * 脱敏银行卡号
     * 示例：6222021234567890123 -> 622202***********123
     */
    fun redactBankCard(cardNumber: String): String {
        if (cardNumber.length < 10) return cardNumber
        val start = 6
        val end = cardNumber.length - 3
        return cardNumber.replaceRange(start, end, "*".repeat(end - start))
    }
}
