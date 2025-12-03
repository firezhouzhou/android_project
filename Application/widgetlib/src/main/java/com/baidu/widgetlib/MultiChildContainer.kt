package com.baidu.widgetlib

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * 多子控件容器
 * 支持动态添加、删除、管理多个子控件
 * 
 * 特性：
 * - 支持水平/垂直布局
 * - 支持子控件间距设置
 * - 支持子控件对齐方式
 * - 支持动态添加/删除子控件
 */
class MultiChildContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        const val ORIENTATION_HORIZONTAL = 0
        const val ORIENTATION_VERTICAL = 1
        
        const val GRAVITY_START = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_END = 2
    }

    // 布局方向：水平或垂直
    var orientation: Int = ORIENTATION_HORIZONTAL
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    // 子控件间距
    var childSpacing: Int = 0
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    // 子控件对齐方式
    var childGravity: Int = GRAVITY_START
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    init {
        // 读取自定义属性
        context.obtainStyledAttributes(attrs, R.styleable.MultiChildContainer).apply {
            orientation = getInt(R.styleable.MultiChildContainer_orientation, ORIENTATION_HORIZONTAL)
            childSpacing = getDimensionPixelSize(R.styleable.MultiChildContainer_childSpacing, 0)
            childGravity = getInt(R.styleable.MultiChildContainer_childGravity, GRAVITY_START)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxWidth = 0
        var maxHeight = 0
        var totalWidth = 0
        var totalHeight = 0

        // 测量所有子控件
        children.forEach { child ->
            if (child.visibility != View.GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                
                if (orientation == ORIENTATION_HORIZONTAL) {
                    totalWidth += child.measuredWidth
                    maxHeight = maxHeight.coerceAtLeast(child.measuredHeight)
                } else {
                    maxWidth = maxWidth.coerceAtLeast(child.measuredWidth)
                    totalHeight += child.measuredHeight
                }
            }
        }

        // 添加间距
        val visibleChildCount = children.count { it.visibility != View.GONE }
        if (visibleChildCount > 1) {
            val totalSpacing = childSpacing * (visibleChildCount - 1)
            if (orientation == ORIENTATION_HORIZONTAL) {
                totalWidth += totalSpacing
            } else {
                totalHeight += totalSpacing
            }
        }

        // 添加padding
        totalWidth += paddingLeft + paddingRight
        totalHeight += paddingTop + paddingBottom
        maxWidth += paddingLeft + paddingRight
        maxHeight += paddingTop + paddingBottom

        // 确定最终尺寸
        val finalWidth = if (orientation == ORIENTATION_HORIZONTAL) totalWidth else maxWidth
        val finalHeight = if (orientation == ORIENTATION_VERTICAL) totalHeight else maxHeight

        setMeasuredDimension(
            resolveSize(finalWidth, widthMeasureSpec),
            resolveSize(finalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentWidth = r - l
        val parentHeight = b - t

        if (orientation == ORIENTATION_HORIZONTAL) {
            layoutHorizontal(parentWidth, parentHeight)
        } else {
            layoutVertical(parentWidth, parentHeight)
        }
    }

    private fun layoutHorizontal(parentWidth: Int, parentHeight: Int) {
        var currentX = paddingLeft
        val startY = paddingTop

        children.forEach { child ->
            if (child.visibility != View.GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                // 根据gravity计算Y坐标
                val childY = when (childGravity) {
                    GRAVITY_CENTER -> startY + (parentHeight - paddingTop - paddingBottom - childHeight) / 2
                    GRAVITY_END -> parentHeight - paddingBottom - childHeight
                    else -> startY
                }

                child.layout(currentX, childY, currentX + childWidth, childY + childHeight)
                currentX += childWidth + childSpacing
            }
        }
    }

    private fun layoutVertical(parentWidth: Int, parentHeight: Int) {
        val startX = paddingLeft
        var currentY = paddingTop

        children.forEach { child ->
            if (child.visibility != View.GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                // 根据gravity计算X坐标
                val childX = when (childGravity) {
                    GRAVITY_CENTER -> startX + (parentWidth - paddingLeft - paddingRight - childWidth) / 2
                    GRAVITY_END -> parentWidth - paddingRight - childWidth
                    else -> startX
                }

                child.layout(childX, currentY, childX + childWidth, currentY + childHeight)
                currentY += childHeight + childSpacing
            }
        }
    }

    /**
     * 添加子控件
     */
    fun addChildView(view: View) {
        addView(view)
    }

    /**
     * 批量添加子控件
     */
    fun addChildViews(vararg views: View) {
        views.forEach { addView(it) }
    }

    /**
     * 移除指定位置的子控件
     */
    fun removeChildAt(index: Int) {
        if (index in 0 until childCount) {
            removeViewAt(index)
        }
    }

    /**
     * 清空所有子控件
     */
    fun clearChildren() {
        removeAllViews()
    }

    /**
     * 获取子控件数量
     */
    fun getChildrenCount(): Int = childCount
}