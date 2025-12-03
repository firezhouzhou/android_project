package com.baidu.application

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * GridLayout间距装饰器
 * 实现item间距均分，首尾item紧贴RecyclerView边缘
 *
 * @param spanCount 列数
 * @param spacing 间距大小(dp)
 * @param includeEdge 是否包含边缘（设为false让首尾item紧贴边缘）
 */
class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount // 当前列索引

        if (includeEdge) {
            // 包含边缘的情况（不使用）
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            // 不包含边缘，实现首尾item紧贴边缘，中间均分
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount

            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}