package com.baidu.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_root)
        
        // 设置GridLayoutManager，5列
        val spanCount = 5
        recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        
        // 设置间距装饰器
        // spacing单位是px，这里转换dp到px
        val spacingInPixels = (12 * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = spanCount,
                spacing = spacingInPixels,
                includeEdge = false // 不包含边缘，让首尾item紧贴RecyclerView
            )
        )
        
        // 设置适配器 - 2行5列，共10个item
        recyclerView.adapter = GridAdapter(itemCount = 10)
    }
}