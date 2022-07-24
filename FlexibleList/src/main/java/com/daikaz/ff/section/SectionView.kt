package com.daikaz.ff.section

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Size
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyControllerAdapter
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.FlexibleListFragment
import com.daikaz.ff.LoadStatus
import com.daikaz.ff.epoxy.model.FlexibleListEpoxyModel

abstract class SectionView<D> {

    /**
     * span size of recycler view. Do not change it in children's class
     */
    var totalSpanCount: Int = 0
        internal set

    internal var containerSize: Size = Size(0, 0)

    internal var startIndex: Int = 0
    internal var endIndex: Int = 0

    open val itemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                if (position !in startIndex .. endIndex) return
                val adapter = parent.adapter
                if (adapter is EpoxyControllerAdapter) {
                    val model = adapter.getModelAtPosition(position)
                    if (model !is FlexibleListEpoxyModel<*>) {
                        return // TODO: Handle for carousel
                        throw Exception("FlexibleList only works with FlexibleListEpoxyModel<*>. Currently, it was $model")
                    }
                    if (model.sectionID != viewModel.sectionID) {
                        return
                    }
                    getItemOffsets(model, position, outRect, view, parent, state)
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                this@SectionView.onDraw(c, parent, state)
            }

            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDrawOver(c, parent, state)
                this@SectionView.onDrawOver(c, parent, state)
            }
        }
    }

    fun getContext() = fragment.context
    fun containerWidth() = containerSize.width
    fun containerHeight() = containerSize.height

    abstract val viewModel: SectionViewModel<D>
    abstract val fragment: FlexibleListFragment<*, *>

    internal open fun buildEpoxyModels(status: LoadStatus, data: D? = null): List<EpoxyModel<*>> = with(status) {
        when (this) {
            LoadStatus.LOADING -> buildLoadingModels()
            LoadStatus.ERROR   -> buildErrorModels()
            LoadStatus.SUCCESS -> buildSuccessModels(data)
        }.run {
            map {
                if (it is FlexibleListEpoxyModel<*>) {
                    it.sectionID = viewModel.sectionID
                }
                it.modifyModelBySectionView()
            }
        }
    }

    internal open fun <T> EpoxyModel<T>.modifyModelBySectionView(): EpoxyModel<T> = this

    // region Build epoxy models
    protected abstract fun buildSuccessModels(data: D?, idPrefix: String = "SUCCESS_${viewModel.sectionID}_"): List<EpoxyModel<*>>
    protected open fun buildLoadingModels(idPrefix: String = "SHIMMER_${viewModel.sectionID}_"): List<EpoxyModel<*>> = emptyList()
    protected open fun buildErrorModels(idPrefix: String = "ERROR_${viewModel.sectionID}_"): List<EpoxyModel<*>> = emptyList()
    // endregion

    // region Item decoration section
    open fun getItemOffsets(model: EpoxyModel<*>?, position: Int, outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit
    open fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) = Unit
    open fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) = Unit
    // endregion
}