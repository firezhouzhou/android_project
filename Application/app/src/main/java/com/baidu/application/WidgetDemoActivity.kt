package com.baidu.application

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baidu.widgetlib.ColorfulButton
import com.baidu.widgetlib.IconTextView
import com.baidu.widgetlib.MultiChildContainer

/**
 * Widget库使用示例Activity
 */
class WidgetDemoActivity : AppCompatActivity() {

    private lateinit var containerHorizontal: MultiChildContainer
    private lateinit var containerVertical: MultiChildContainer
    private lateinit var containerDynamic: MultiChildContainer
    private var dynamicChildCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_demo)

        initViews()
        setupHorizontalContainer()
        setupVerticalContainer()
    }

    private fun initViews() {
        containerHorizontal = findViewById(R.id.container_horizontal)
        containerVertical = findViewById(R.id.container_vertical)
        containerDynamic = findViewById(R.id.container_dynamic)

        findViewById<Button>(R.id.btn_add_child).setOnClickListener {
            addDynamicChild()
        }

        findViewById<Button>(R.id.btn_clear_children).setOnClickListener {
            containerDynamic.clearChildren()
            dynamicChildCount = 0
        }
    }

    /**
     * 设置水平布局容器
     */
    private fun setupHorizontalContainer() {
        // 添加彩色按钮
        val colors = listOf(
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#45B7D1")
        )

        colors.forEachIndexed { index, color ->
            val button = ColorfulButton(this).apply {
                text = "按钮${index + 1}"
                setButtonBackgroundColor(color)
                setOnClickListener {
                    // 点击事件
                }
            }
            containerHorizontal.addChildView(button)
        }
    }

    /**
     * 设置垂直布局容器
     */
    private fun setupVerticalContainer() {
        // 添加图标文字组合控件
        val items = listOf("首页", "消息", "我的")
        
        items.forEach { title ->
            val iconTextView = IconTextView(this).apply {
                setText(title)
                setTextColor(Color.parseColor("#333333"))
                // 这里可以设置图标，如果有资源的话
                // setIcon(R.drawable.ic_xxx)
            }
            containerVertical.addChildView(iconTextView)
        }
    }

    /**
     * 动态添加子控件
     */
    private fun addDynamicChild() {
        dynamicChildCount++
        
        val textView = TextView(this).apply {
            text = "Item $dynamicChildCount"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(24, 12, 24, 12)
            setBackgroundColor(getRandomColor())
        }
        
        containerDynamic.addChildView(textView)
    }

    /**
     * 获取随机颜色
     */
    private fun getRandomColor(): Int {
        val colors = listOf(
            Color.parseColor("#FF6B6B"),
            Color.parseColor("#4ECDC4"),
            Color.parseColor("#45B7D1"),
            Color.parseColor("#FFA07A"),
            Color.parseColor("#98D8C8")
        )
        return colors.random()
    }
}