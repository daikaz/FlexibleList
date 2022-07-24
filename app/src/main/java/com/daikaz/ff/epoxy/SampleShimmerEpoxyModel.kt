package com.daikaz.ff.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.daikaz.ff.R
import com.daikaz.ff.epoxy.model.FlexibleListDebuggableEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyHolder
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel

@EpoxyModelClass(layout = R.layout.layout_item_sample_shimmer)
abstract class SampleShimmerEpoxyModel : FlexibleListEpoxyModel<SampleShimmerEpoxyModel.ViewHolder>(), FlexibleListDebuggableEpoxyModel {

    @EpoxyAttribute
    override var debugName: String? = null

    class ViewHolder : FlexibleListEpoxyHolder()
}
