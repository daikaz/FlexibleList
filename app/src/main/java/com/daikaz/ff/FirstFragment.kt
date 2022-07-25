package com.daikaz.ff

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.daikaz.ff.databinding.FragmentFirstBinding
import com.daikaz.ff.sample.*
import com.daikaz.ff.section.SectionView
import com.daikaz.ff.section.SectionViewModel

class FirstFragment : FlexibleListFragment<FragmentFirstBinding, FirstFragmentViewModel>() {

    override val viewModel: FirstFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val metadataRetriever = MediaMetadataRetriever()
        //metadataRetriever.setDataSource()
    }

    override fun getSectionViewFrom(viewModel: SectionViewModel<*, *, *>): SectionView<*>? {
        return when (viewModel) {
            is SampleVerticalSectionViewModel         -> SampleVerticalSectionView(viewModel, this)
            is SampleSameSizeGridSectionViewModel     -> SampleSameSizeGridSectionView(viewModel, this)
            is SampleSameSizeCarouselSectionViewModel -> SampleSameSizeCarouselSectionView(viewModel, this)
            else                                      -> null
        }
    }

    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): FragmentFirstBinding {
        return FragmentFirstBinding.inflate(inflater, container, false)
    }

    override fun recyclerView(): EpoxyRecyclerView = binding.recyclerViewID
    override fun emptyView(): View = binding.tvEmptyID
    override fun swipeRefreshLayout(): SwipeRefreshLayout = binding.swipeRefreshLayoutID
    override fun errorViewGroup(): View = binding.vErrorGroupID
    override fun reloadButton(): View = binding.btnReloadID
}