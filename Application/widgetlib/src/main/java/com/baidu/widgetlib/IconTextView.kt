package com.baidu.widgetlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 图标文字组合控件 - 示例子控件
 * 包含一个图标和一个文字标签
 */
class IconTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView: ImageView
    private val textView: TextView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        // 创建图标
        iconView = ImageView(context).apply {
            layoutParams = LayoutParams(80, 80)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
        addView(iconView)

        // 创建文字
        textView = TextView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            textSize = 14f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
        addView(textView)
    }

    /**
     * 设置图标资源
     */
    fun setIcon(resId: Int) {
        iconView.setImageResource(resId)
    }

    /**
     * 设置文字
     */
    fun setText(text: String) {
        textView.text = text
    }

    /**
     * 设置文字颜色
     */
    fun setTextColor(color: Int) {
        textView.setTextColor(color)
    }
}