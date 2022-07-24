package com.daikaz.ff.epoxy

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.daikaz.ff.R
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyHolder
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHeaderEpoxyModel

@EpoxyModelClass(layout = R.layout.layout_item_sample_sticky)
abstract class SampleStickyEpoxyModel : FlexibleListEpoxyModel<SampleStickyEpoxyModel.ViewHolder>(), FlexibleListHeaderEpoxyModel, FlexibleListHardCodedSizeEpoxyModel {

    @EpoxyAttribute
    lateinit var title: String

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.tvContentID.text = title
    }

    class ViewHolder : FlexibleListEpoxyHolder() {

        lateinit var tvContentID: AppCompatTextView
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            tvContentID = itemView.findViewById(R.id.tvContentID)
        }
    }
}