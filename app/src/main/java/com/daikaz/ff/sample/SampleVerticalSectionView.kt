package com.daikaz.ff.sample

import android.view.View
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.FlexibleListFragment
import com.daikaz.ff.epoxy.*
import com.daikaz.ff.section.SectionViewModel
import com.daikaz.ff.section.SimpleVerticalSectionView

class SampleVerticalSectionView(
    override val viewModel: SampleVerticalSectionViewModel,
    override val fragment: FlexibleListFragment<*, *>
) : SimpleVerticalSectionView<Int>() {

    override fun buildSuccessModels(data: Int?, idPrefix: String): List<EpoxyModel<*>> {
        val n = data ?: 0
        val models = arrayListOf<EpoxyModel<*>>()
        models.add(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
        )
        for (i in 0 until n) {
            models.add(
                SampleEpoxyModel_()
                    .id("${idPrefix}$i")
                    .name(viewModel.sectionID)
            )
        }
        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}SPACE_BOTTOM_")
        )
        return models
    }

    override fun buildLoadingModels(idPrefix: String): List<EpoxyModel<*>> {
        val models = arrayListOf<EpoxyModel<*>>()
        models.add(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
        )
        for (i in 0 until 6) {
            models.add(SampleShimmerEpoxyModel_().id("${idPrefix}$i"))
        }
        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}SPACE_BOTTOM_")
        )
        return models
    }

    override fun buildErrorModels(idPrefix: String): List<EpoxyModel<*>> {
        val models = arrayListOf<EpoxyModel<*>>()
        models.add(
            SampleStickyEpoxyModel_()
                .id("${idPrefix}HEADER")
                .title("Header of ${viewModel.sectionID}")
        )

        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}TOP_SPACE")
        )

        models.add(
            SampleErrorEpoxyModel_()
                .itemWidthPixels(containerWidth())
                .onReloadClick(View.OnClickListener {
                    viewModel.dispatchIntent(1)
                })
                .id(idPrefix)
                .sectionID(viewModel.sectionID)
        )

        models.add(
            FlexibleListDynamicSpaceEpoxyModel_()
                .heightDps(4f)
                .id("${idPrefix}SPACE")
        )
        return models
    }

}