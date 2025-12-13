package com.baidu.application.analytics.upload

import com.baidu.application.analytics.AnalyticsConfig
import com.baidu.application.analytics.model.EventModel
import com.baidu.application.analytics.util.CompressionUtil
import com.baidu.application.analytics.util.CryptoUtil
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * HTTP 上传器
 *
 * 使用 HttpURLConnection 上传事件数据
 */
class HttpUploader(
    private val config: AnalyticsConfig
) : IUploader {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun upload(events: List<EventModel>): Result<Unit> {
        return try {
            // 1. 序列化事件数据
            val jsonData = json.encodeToString(events)

            // 2. 压缩数据
            val compressedData = if (config.enableCompression) {
                CompressionUtil.compress(jsonData)
            } else {
                jsonData.toByteArray()
            }

            // 3. 加密数据
            val finalData = if (config.enableEncryption) {
                CryptoUtil.encrypt(compressedData, config.appKey)
            } else {
                compressedData
            }

            // 4. 发送 HTTP 请求
            val response = sendHttpRequest(finalData)

            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Upload failed: ${response.message}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 发送 HTTP 请求
     */
    private fun sendHttpRequest(data: ByteArray): UploadResponse {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(config.uploadUrl)
            connection = url.openConnection() as HttpURLConnection

            // 设置请求方法
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true

            // 设置请求头
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.setRequestProperty("App-Key", config.appKey)
            connection.setRequestProperty("Content-Length", data.size.toString())
            connection.setRequestProperty("Accept", "application/json")

            if (config.enableCompression) {
                connection.setRequestProperty("Content-Encoding", "gzip")
            }

            if (config.enableEncryption) {
                connection.setRequestProperty("X-Encrypted", "true")
            }

            // 设置超时
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            // 写入数据
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(data)
            outputStream.flush()
            outputStream.close()

            // 读取响应
            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                return UploadResponse(
                    isSuccess = true,
                    code = responseCode,
                    message = response
                )
            } else {
                return UploadResponse(
                    isSuccess = false,
                    code = responseCode,
                    message = responseMessage
                )
            }

        } catch (e: Exception) {
            return UploadResponse(
                isSuccess = false,
                code = -1,
                message = e.message ?: "Unknown error"
            )
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * 上传响应
     */
    private data class UploadResponse(
        val isSuccess: Boolean,
        val code: Int,
        val message: String
    )
}
