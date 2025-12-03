package com.baidu.widgetlib

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 * 彩色按钮 - 示例子控件
 * 支持自定义背景色、圆角、文字颜色
 */
class ColorfulButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var bgColor: Int = Color.parseColor("#4ECDC4")
    private var cornerRadius: Float = 16f

    init {
        setupButton()
    }

    private fun setupButton() {
        val drawable = GradientDrawable().apply {
            setColor(bgColor)
            this.cornerRadius = this@ColorfulButton.cornerRadius
        }
        background = drawable
        setTextColor(Color.WHITE)
        setPadding(32, 16, 32, 16)
    }

    /**
     * 设置按钮背景颜色
     */
    fun setButtonBackgroundColor(color: Int) {
        bgColor = color
        setupButton()
    }

    /**
     * 设置圆角半径
     */
    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        setupButton()
    }
}