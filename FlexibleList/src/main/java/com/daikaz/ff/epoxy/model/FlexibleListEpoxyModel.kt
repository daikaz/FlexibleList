package com.daikaz.ff.epoxy.model

import android.content.Context
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.daikaz.ff.utils.toPx

abstract class FlexibleListEpoxyModel<T : FlexibleListEpoxyHolder> : EpoxyModelWithHolder<T>() {

    @EpoxyAttribute
    open var itemWidthPixels: Int = -100_00_99_88

    @EpoxyAttribute
    open var itemWidthDps: Float = -100_00_99_88f

    @EpoxyAttribute
    open lateinit var sectionID: String

    protected lateinit var context: Context

    override fun bind(holder: T) {
        super.bind(holder)

        context = holder.rootView.context

        if (itemWidthPixels != -100_00_99_88) {
            val params = holder.rootView.layoutParams as ViewGroup.LayoutParams
            params.width = itemWidthPixels
            holder.rootView.layoutParams = params
        }

        if (itemWidthDps != -100_00_99_88f) {
            val params = holder.rootView.layoutParams as ViewGroup.LayoutParams
            params.width = if (itemWidthDps == ViewGroup.LayoutParams.MATCH_PARENT.toFloat()) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else {
                itemWidthDps.toPx(context)
            }
            holder.rootView.layoutParams = params
        }
    }
}