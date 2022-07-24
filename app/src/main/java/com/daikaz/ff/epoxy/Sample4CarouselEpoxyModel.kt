package com.daikaz.ff.epoxy

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.daikaz.ff.R
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyHolder
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel

@EpoxyModelClass(layout = R.layout.layout_item_sample_for_carousel)
abstract class Sample4CarouselEpoxyModel : FlexibleListEpoxyModel<Sample4CarouselEpoxyModel.ViewHolder>() {

    @EpoxyAttribute
    lateinit var name: String

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.tvContentID.text = "$name > hi hi"
    }

    class ViewHolder : FlexibleListEpoxyHolder() {

        lateinit var tvContentID: AppCompatTextView
        override fun bindView(itemView: View) {
            rootView = itemView.findViewById(R.id.rootID)
            tvContentID = itemView.findViewById(R.id.tvContentID)
        }
    }
}