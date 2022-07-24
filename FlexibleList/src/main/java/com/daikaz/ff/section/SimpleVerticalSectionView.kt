package com.daikaz.ff.section

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel
import com.daikaz.ff.utils.maxSpanCount
import com.daikaz.ff.utils.toPx

abstract class SimpleVerticalSectionView<D> : SectionView<D>() {

    open val paddingStart: Int = 0

    open val paddingEnd: Int = 0

    open val verticalItemSpacing: Int get() = 4f.toPx(getContext())

    override fun <T> EpoxyModel<T>.modifyModelBySectionView(): EpoxyModel<T> {
        if (this is FlexibleListEpoxyModel<*>) {
            itemWidthPixels = RecyclerView.LayoutParams.MATCH_PARENT
        }
        return this.maxSpanCount()
    }

    override fun getItemOffsets(model: EpoxyModel<*>?, position: Int, outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (model is FlexibleListHardCodedSizeEpoxyModel) {
            return
        }

        outRect.left = paddingStart
        outRect.right = paddingEnd
        outRect.top = verticalItemSpacing
    }
}
