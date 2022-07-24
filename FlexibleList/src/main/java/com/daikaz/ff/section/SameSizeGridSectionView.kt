package com.daikaz.ff.section

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel
import com.daikaz.ff.utils.overrideSpanCount
import com.daikaz.ff.utils.toPx

/**
 * The fixture below shows how `SameSizeGridSectionView` works with `numberOfColumns = 3`
 *
 *           ╔════════════════════════════╗
 *           ║ Section Header             ║  ← Header: optional (must implement `FlexibleListHardCodedSizeEpoxyModel`)
 *           ║▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒║  ← verticalItemSpacing applied to item's `outRect.top`
 *           ║▒        ▒        ▒        ▒║
 *           ║▒  Item  ▒  Item  ▒  Item  ▒║
 *           ║▒        ▒        ▒        ▒║
 *           ║▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒║  ← verticalItemSpacing applied to item's `outRect.top`
 *           ║▒        ▒        ▒        ▒║
 *           ║▒  Item  ▒  Item  ▒  Item  ▒║  There is no verticalItemSpacing at the bottom of section
 *           ║▒        ▒        ▒        ▒║ ↲
 *           ↗         ↑        ↑         ↖
 *  paddingStart  horizontalItemSpacing   paddingEnd

 *  Note:
 *  - Header's (or Error's) `EpoxyModel<*>` must implement `FlexibleListHardCodedSizeEpoxyModel` by using `maxSpanCount()` or `overrideSpanCount()`.
 *  - The `EpoxyModel<*>` of item must extend `FlexibleListEpoxyModel<*>` to automatically apply same `spanSize`
 *  - Item's `EpoxyModel<*>` must implement `FlexibleListHardCodedSizeEpoxyModel` to control its own `spanSize` (if needed) by using `overrideSpanCount()`.
 */
abstract class SameSizeGridSectionView<D> : SectionView<D>() {

    abstract val numberOfColumns: Int

    open val paddingStart: Int = 0

    open val paddingEnd: Int = 0

    open val horizontalItemSpacing: Int get() = 4f.toPx(getContext())

    open val verticalItemSpacing: Int get() = 4f.toPx(getContext())

    override fun <T> EpoxyModel<T>.modifyModelBySectionView(): EpoxyModel<T> {
        if (fragment.recyclerView().layoutManager !is GridLayoutManager) {
            throw Exception("SameSizeGridSectionView only works with GridLayoutManager")
        }
        if (this is FlexibleListHardCodedSizeEpoxyModel) {
            return this
        }

        if (this is FlexibleListEpoxyModel<*>) {
            // Force MATCH_PARENT to make sure it is correctly when RecyclerView recycles ViewHolder
            itemWidthPixels = RecyclerView.LayoutParams.MATCH_PARENT
        }

        return this.overrideSpanCount(totalSpanCount / numberOfColumns)
    }

    override fun getItemOffsets(model: EpoxyModel<*>?, position: Int, outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        if (model is FlexibleListHardCodedSizeEpoxyModel) {
            return
        }
        Log.w("seseseseti", "position: $position start: $startIndex, end: $endIndex")

        val layout = parent.layoutManager as? GridLayoutManager ?: return

        outRect.setEmpty()

        val spanSizeLookup = layout.spanSizeLookup

        val isInFirstColumn = isInFirstColumn(position, spanSizeLookup)
        val isInLastColumn = isInLastColumn(position, spanSizeLookup)

        Log.d("seseseseti", "position: $position [$isInFirstColumn,$isInLastColumn] model: $model")

        if (isInFirstColumn) {
            outRect.left = paddingStart
            outRect.right = horizontalItemSpacing / 2
        }
        if (!isInFirstColumn && !isInLastColumn) {
            outRect.left = horizontalItemSpacing / 2
            outRect.right = horizontalItemSpacing / 2
        }
        if (isInLastColumn) {
            outRect.left = horizontalItemSpacing / 2
            outRect.right = paddingEnd
        }

        outRect.top = verticalItemSpacing
    }

    private fun isInFirstColumn(position: Int, spanSizeLookup: GridLayoutManager.SpanSizeLookup): Boolean {
        var totalSpan = 0
        for (i in startIndex until position) {
            totalSpan += spanSizeLookup.getSpanSize(i)
        }
        return totalSpan % totalSpanCount == 0
    }

    private fun isInLastColumn(position: Int, spanSizeLookup: GridLayoutManager.SpanSizeLookup): Boolean {
        var totalSpan = 0
        for (i in startIndex..position) {
            totalSpan += spanSizeLookup.getSpanSize(i)
        }

        return totalSpan % totalSpanCount == 0
    }

}