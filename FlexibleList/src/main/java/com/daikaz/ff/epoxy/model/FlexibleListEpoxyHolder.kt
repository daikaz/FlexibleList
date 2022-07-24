package com.daikaz.ff.epoxy.model

import android.view.View
import com.airbnb.epoxy.EpoxyHolder

abstract class FlexibleListEpoxyHolder : EpoxyHolder() {

    lateinit var rootView: View

    override fun bindView(itemView: View) {
        rootView = itemView.rootView
    }

}