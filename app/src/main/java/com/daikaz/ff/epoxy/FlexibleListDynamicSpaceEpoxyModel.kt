package com.daikaz.ff.epoxy

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.daikaz.ff.R
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyHolder
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel
import com.daikaz.ff.utils.toPx

@EpoxyModelClass(layout = R.layout.layout_item_dynamic_height)
abstract class FlexibleListDynamicSpaceEpoxyModel : FlexibleListEpoxyModel<FlexibleListDynamicSpaceEpoxyModel.ViewHolder>(),
    FlexibleListHardCodedSizeEpoxyModel {

    @EpoxyAttribute
    var height: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    @EpoxyAttribute
    var heightPixels: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    @EpoxyAttribute
    var heightDps: Float = ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()

    @EpoxyAttribute
    var color: Int = Color.TRANSPARENT

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        val context = holder.vSpace.context
        val params = holder.vSpace.layoutParams
        if (heightPixels != ViewGroup.LayoutParams.WRAP_CONTENT) {
            params.height = height
        } else if (heightDps != ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()) {
            params.height = heightDps.toPx(context)
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        holder.vSpace.layoutParams = params

        holder.vSpace.setBackgroundColor(color)
    }

    class ViewHolder : FlexibleListEpoxyHolder() {

        lateinit var vSpace: View
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            itemView.findViewById<View?>(R.id.vSpaceID)?.let {
                vSpace = it
            }
        }
    }
}