package com.daikaz.ff.configs

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager

data class LayoutConfiguration(
    val layoutManager: RecyclerView.LayoutManager,
    val totalSpanCount: Int,
) {

    companion object {
        fun defaultLinearLayoutConfig(context: Context) = LayoutConfiguration(
            LinearLayoutManager(context, RecyclerView.VERTICAL, false),
            1,
        )

        fun defaultGridLayoutConfig(context: Context, totalSpanCount: Int = 12) = LayoutConfiguration(
            GridLayoutManager(context, totalSpanCount),
            totalSpanCount,
        )

        fun defaultStickyHeaderLayoutConfig(context: Context) = LayoutConfiguration(
            StickyHeaderLinearLayoutManager(context),
            1,
        )
    }
}
