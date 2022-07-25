package com.daikaz.ff.sample

import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.FlexibleListFragment
import com.daikaz.ff.SectionItemDisplayType
import com.daikaz.ff.epoxy.FlexibleListDynamicSpaceEpoxyModel_
import com.daikaz.ff.section.SectionViewModel
import com.daikaz.ff.epoxy.SampleEpoxyModel_
import com.daikaz.ff.epoxy.SampleShimmerEpoxyModel_
import com.daikaz.ff.epoxy.SampleStickyEpoxyModel_
import com.daikaz.ff.section.SameSizeGridSectionView
import com.daikaz.ff.utils.maxSpanCount
import com.daikaz.ff.utils.toPx

class SampleSameSizeGridSectionView(
    override val viewModel: SampleSameSizeGridSectionViewModel,
    override val fragment: FlexibleListFragment<*, *>
) : SameSizeGridSectionView<Int>() {

    override val numberOfColumns: Int = 3

    override val paddingStart: Int
        get() = 0f.toPx(getContext())

    override val paddingEnd: Int
        get() = 0f.toPx(getContext())

    override val horizontalItemSpacing: Int
        get() = 4f.toPx(getContext())

    override val verticalItemSpacing: Int
        get() = 4f.toPx(getContext())

    override fun buildSuccessModels(data: Int?, idPrefix: String): List<EpoxyModel<*>> {
        val n = data ?: 0
        val models = arrayListOf<EpoxyModel<*>>()
        models.add(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
                .maxSpanCount()
        )
        for (i in 0 until n) {
            models.add(
                SampleEpoxyModel_()
                    .id("${idPrefix}$i")
                    .name("${viewModel.sectionID} < $i")
            )
        }
        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}SPACE_BOTTOM")
                .maxSpanCount()
        )
        return models
    }

    override fun buildLoadingModels(idPrefix: String): List<EpoxyModel<*>> {
        val n = 12
        val models = arrayListOf<EpoxyModel<*>>()
        models.add(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
                .maxSpanCount()
        )
        for (i in 0 until n) {
            models.add(
                SampleShimmerEpoxyModel_()
                    .id("${idPrefix}$i")
            )
        }
        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}SPACE_BOTTOM")
                .maxSpanCount()
        )
        return models
    }
}
