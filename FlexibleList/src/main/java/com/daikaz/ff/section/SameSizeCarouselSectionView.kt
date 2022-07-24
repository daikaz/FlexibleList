package com.daikaz.ff.section

import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.LoadStatus
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHardCodedSizeEpoxyModel
import com.daikaz.ff.utils.maxSpanCount
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

abstract class SameSizeCarouselSectionView<D> : SectionView<D>() {

    abstract val itemPreferWidth: Int
    abstract val itemSpacingDp: Int
    abstract val paddingDp: Rect
    open val snapHelper: SnapHelper by lazy { GravitySnapHelper(Gravity.START) }

    open fun buildHeaderModels(data: D?, idPrefix: String, isLoading: Boolean): List<EpoxyModel<*>> = emptyList()
    open fun buildBottomModels(data: D?, idPrefix: String, isLoading: Boolean): List<EpoxyModel<*>> = emptyList()

    override fun buildEpoxyModels(status: LoadStatus, data: D?): List<EpoxyModel<*>> {
        val models = ArrayList<EpoxyModel<*>>()
        models.addAll(when (status) {
            LoadStatus.LOADING -> buildHeaderModels(data, "SHIMMER_${viewModel.sectionID}_HEADER", true)
            LoadStatus.ERROR   -> emptyList()
            LoadStatus.SUCCESS -> buildHeaderModels(data, "SUCCESS_${viewModel.sectionID}_HEADER", false)
        }.map {
            if (it is FlexibleListEpoxyModel<*>) {
                it.sectionID = viewModel.sectionID
            }
            it.modifyModelBySectionView()
        })
        when (status) {
            LoadStatus.LOADING,
            LoadStatus.SUCCESS -> {
                Carousel.setDefaultGlobalSnapHelperFactory(null)
                models.add(
                    CarouselModel_()
                        .id(viewModel.sectionID)
                        .padding(Carousel.Padding.dp(paddingDp.left, paddingDp.top, paddingDp.right, paddingDp.bottom, itemSpacingDp))
                        .models(super.buildEpoxyModels(status, data))
                        .onBind { _, view, _ ->
                            snapHelper.attachToRecyclerView(view)
                        }
                        .maxSpanCount()

                )
            }
            LoadStatus.ERROR   -> {
                models.addAll(super.buildEpoxyModels(status, data))
            }
        }
        models.addAll(when (status) {
            LoadStatus.LOADING -> buildBottomModels(data, "SHIMMER_${viewModel.sectionID}_BOTTOM", true)
            LoadStatus.ERROR   -> emptyList()
            LoadStatus.SUCCESS -> buildBottomModels(data, "SUCCESS_${viewModel.sectionID}_BOTTOM", false)
        }.map {
            if (it is FlexibleListEpoxyModel<*>) {
                it.sectionID = viewModel.sectionID
            }
            it.modifyModelBySectionView()
        })
        return models
    }

    override fun <T> EpoxyModel<T>.modifyModelBySectionView(): EpoxyModel<T> {
        if (this is FlexibleListHardCodedSizeEpoxyModel) {
            return this
        }
        if (this is FlexibleListEpoxyModel<*>) {
            itemWidthPixels = itemPreferWidth
        }
        return this
    }

    override fun getItemOffsets(model: EpoxyModel<*>?, position: Int, outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(model, position, outRect, view, parent, state)
        //
    }
}