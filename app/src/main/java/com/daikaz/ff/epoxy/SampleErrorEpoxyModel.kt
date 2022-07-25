package com.daikaz.ff.epoxy

import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.daikaz.ff.R
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyHolder
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel

@EpoxyModelClass
abstract class SampleErrorEpoxyModel : FlexibleListEpoxyModel<SampleErrorEpoxyModel.ViewHolder>(), FlexibleListHardCodedSizeEpoxyModel {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash, EpoxyAttribute.Option.DoNotUseInToString)
    var onReloadClick: View.OnClickListener? = null

    override fun getDefaultLayout(): Int = R.layout.layout_item_section_error

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        holder.btnReloadID.setOnClickListener(onReloadClick)
    }

    class ViewHolder : FlexibleListEpoxyHolder() {

        lateinit var btnReloadID: AppCompatButton
        override fun bindView(itemView: View) {
            super.bindView(itemView)
            btnReloadID = itemView.findViewById(R.id.btnReloadID)
        }
    }
}
