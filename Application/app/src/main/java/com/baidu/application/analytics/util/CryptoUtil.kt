package com.baidu.application.analytics.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 加密工具类
 */
object CryptoUtil {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    /**
     * AES 加密
     */
    fun encrypt(data: ByteArray, key: String): ByteArray {
        val secretKey = generateKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

    /**
     * AES 加密（字符串）
     */
    fun encrypt(data: String, key: String): String {
        val encrypted = encrypt(data.toByteArray(Charsets.UTF_8), key)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    /**
     * AES 解密
     */
    fun decrypt(data: ByteArray, key: String): ByteArray {
        val secretKey = generateKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(data)
    }

    /**
     * AES 解密（字符串）
     */
    fun decrypt(data: String, key: String): String {
        val encrypted = Base64.decode(data, Base64.NO_WRAP)
        val decrypted = decrypt(encrypted, key)
        return String(decrypted, Charsets.UTF_8)
    }

    /**
     * 生成 AES 密钥
     */
    private fun generateKey(key: String): SecretKeySpec {
        // 使用 MD5 将任意长度的 key 转换为 16 字节
        val md = MessageDigest.getInstance("MD5")
        val keyBytes = md.digest(key.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    /**
     * MD5 哈希
     */
    fun md5(data: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(data.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * SHA256 哈希
     */
    fun sha256(data: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
