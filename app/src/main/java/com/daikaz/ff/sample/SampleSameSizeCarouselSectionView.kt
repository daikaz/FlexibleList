package com.daikaz.ff.sample

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.FlexibleListFragment
import com.daikaz.ff.epoxy.FlexibleListDynamicSpaceEpoxyModel_
import com.daikaz.ff.epoxy.Sample4CarouselEpoxyModel_
import com.daikaz.ff.epoxy.SampleStickyEpoxyModel_
import com.daikaz.ff.section.SameSizeCarouselSectionView
import com.daikaz.ff.utils.maxSpanCount
import com.daikaz.ff.utils.toPx

class SampleSameSizeCarouselSectionView(
    override val viewModel: SampleSameSizeCarouselSectionViewModel,
    override val fragment: FlexibleListFragment<*, *>
) : SameSizeCarouselSectionView<Int>() {

    override val itemPreferWidth: Int get() = 200f.toPx(getContext())
    override val itemSpacingDp: Int get() = 8
    override val paddingDp: Rect = Rect(8, 4, 8, 4)

    override fun buildHeaderModels(data: Int?, idPrefix: String, isLoading: Boolean): List<EpoxyModel<*>> {
        return listOf(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
                .itemWidthPixels(RecyclerView.LayoutParams.MATCH_PARENT)
                .maxSpanCount()
        )
    }

    //override fun buildBottomModels(data: Int?, idPrefix: String, isLoading: Boolean): List<EpoxyModel<*>> {
    //    return listOf(
    //        FlexibleListDynamicSpaceEpoxyModel_()
    //            .heightDps(4f)
    //            .itemWidthPixels(RecyclerView.LayoutParams.MATCH_PARENT)
    //            .maxSpanCount()
    //            .id("${idPrefix}BOTTOM_SPACE")
    //    )
    //}

    override fun buildSuccessModels(data: Int?, idPrefix: String): List<EpoxyModel<*>> {
        data ?: return emptyList()
        val models = arrayListOf<EpoxyModel<*>>()
        for (i in 0 until data) {
            models.add(
                Sample4CarouselEpoxyModel_().id("${idPrefix}$i").name("${viewModel.sectionID} < $i")
            )
        }
        return models
    }
}