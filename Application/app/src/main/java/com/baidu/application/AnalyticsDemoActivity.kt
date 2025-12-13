package com.baidu.application

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baidu.application.analytics.Analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 埋点系统演示 Activity
 */
class AnalyticsDemoActivity : AppCompatActivity() {

    private lateinit var tvOutput: TextView
    private lateinit var scrollView: ScrollView
    private var privacyEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics_demo)

        initViews()
        setupButtons()
    }

    private fun initViews() {
        tvOutput = findViewById(R.id.tv_analytics_output)
        scrollView = findViewById(R.id.scroll_view)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_basic_event).setOnClickListener {
            testBasicEvent()
        }

        findViewById<Button>(R.id.btn_event_with_params).setOnClickListener {
            testEventWithParams()
        }

        findViewById<Button>(R.id.btn_user_property).setOnClickListener {
            testUserProperty()
        }

        findViewById<Button>(R.id.btn_batch_events).setOnClickListener {
            testBatchEvents()
        }

        findViewById<Button>(R.id.btn_flush).setOnClickListener {
            testFlush()
        }

        findViewById<Button>(R.id.btn_view_stats).setOnClickListener {
            viewStats()
        }

        findViewById<Button>(R.id.btn_clear_cache).setOnClickListener {
            clearCache()
        }

        findViewById<Button>(R.id.btn_privacy_toggle).setOnClickListener {
            togglePrivacy()
        }
    }

    private fun testBasicEvent() {
        Analytics.logEvent("button_click")
        appendOutput("✅ 基础事件已记录: button_click")
    }

    private fun testEventWithParams() {
        val params = mapOf(
            "button_id" to "submit",
            "page" to "demo",
            "timestamp" to System.currentTimeMillis().toString()
        )
        Analytics.logEvent("button_click_with_params", params)
        appendOutput("✅ 带参数事件已记录: button_click_with_params")
    }

    private fun testUserProperty() {
        Analytics.setUserId("user_12345")
        Analytics.setUserProperty("vip_level", "gold")
        Analytics.setUserProperty("age", "25")
        appendOutput("✅ 用户信息已设置: user_12345, vip_level=gold")
    }

    private fun testBatchEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            repeat(100) { index ->
                Analytics.logEvent(
                    "batch_event_$index",
                    mapOf("index" to index.toString())
                )
            }
            withContext(Dispatchers.Main) {
                appendOutput("✅ 已记录 100 个批量事件")
            }
        }
    }

    private fun testFlush() {
        Analytics.flush()
        appendOutput("✅ 已触发立即上传")
    }

    private fun viewStats() {
        CoroutineScope(Dispatchers.Main).launch {
            val stats = withContext(Dispatchers.IO) {
                Analytics.getEventStats()
            }

            val output = buildString {
                appendLine("📊 事件统计信息:")
                appendLine("总数: ${stats["total"]}")
                appendLine("待上传: ${stats["pending"]}")
                appendLine("上传中: ${stats["uploading"]}")
                appendLine("成功: ${stats["success"]}")
                appendLine("失败: ${stats["failed"]}")
            }
            appendOutput(output)
        }
    }

    private fun clearCache() {
        Analytics.clearCache()
        appendOutput("✅ 缓存已清空")
    }

    private fun togglePrivacy() {
        privacyEnabled = !privacyEnabled
        Analytics.setPrivacyEnabled(privacyEnabled)
        appendOutput("✅ 隐私开关: ${if (privacyEnabled) "开启" else "关闭"}")
    }

    private fun appendOutput(text: String) {
        val currentText = tvOutput.text.toString()
        val newText = if (currentText.isEmpty()) {
            text
        } else {
            "$currentText\n\n$text"
        }
        tvOutput.text = newText

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}
