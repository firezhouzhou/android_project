package com.baidu.application

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * GridLayout适配器
 */
class GridAdapter(private val itemCount: Int = 10) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    // 预定义的渐变色彩方案
    private val colors = listOf(
        Color.parseColor("#FF6B6B"), // 珊瑚红
        Color.parseColor("#4ECDC4"), // 青绿色
        Color.parseColor("#45B7D1"), // 天蓝色
        Color.parseColor("#FFA07A"), // 浅橙色
        Color.parseColor("#98D8C8")  // 薄荷绿
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = itemCount

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumber: TextView = itemView.findViewById(R.id.tv_item_number)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        private val cardView: CardView = itemView as CardView

        fun bind(position: Int) {
            tvNumber.text = (position + 1).toString()
            tvTitle.text = "Item ${position + 1}"
            
            // 设置渐变背景色
            val colorIndex = position % colors.size
            itemView.findViewById<View>(R.id.tv_item_number).parent.let {
                if (it is ViewGroup) {
                    it.setBackgroundColor(colors[colorIndex])
                }
            }

            // 添加点击效果
            cardView.setOnClickListener {
                // 可以在这里添加点击事件
            }
        }
    }
}