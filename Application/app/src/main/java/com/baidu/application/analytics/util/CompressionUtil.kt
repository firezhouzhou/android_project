package com.baidu.application.analytics.util

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * 压缩工具类
 */
object CompressionUtil {

    /**
     * 使用 GZIP 压缩字符串
     */
    fun compress(data: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val gzipOutputStream = GZIPOutputStream(byteArrayOutputStream)
        gzipOutputStream.write(data.toByteArray(Charsets.UTF_8))
        gzipOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * 使用 GZIP 压缩字节数组
     */
    fun compress(data: ByteArray): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val gzipOutputStream = GZIPOutputStream(byteArrayOutputStream)
        gzipOutputStream.write(data)
        gzipOutputStream.close()
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * 使用 GZIP 解压缩
     */
    fun decompress(data: ByteArray): String {
        val gzipInputStream = GZIPInputStream(data.inputStream())
        val result = gzipInputStream.readBytes().toString(Charsets.UTF_8)
        gzipInputStream.close()
        return result
    }
}
