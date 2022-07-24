package com.daikaz.ff

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.sample.SampleSameSizeCarouselSectionViewModel
import com.daikaz.ff.sample.SampleSameSizeGridSectionViewModel
import com.daikaz.ff.sample.SampleVerticalSectionViewModel
import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirstFragmentViewModel : FlexibleListViewModel() {

    override fun provideSectionViewModels(): Flow<List<SectionViewModel<*>>> {
        return flow {
            Log.w("FirstFragmentViewModel", "addd+1")
            emit(
                listOf<SectionViewModel<Int>>(
                    SampleVerticalSectionViewModel("A", scope = viewModelScope),
                    SampleSameSizeGridSectionViewModel("B", scope = viewModelScope),
                    SampleVerticalSectionViewModel("C", viewModelScope, SectionConfiguration(SectionConfiguration.ErrorHandleType.SHOW)),
                    SampleSameSizeCarouselSectionViewModel("D", scope = viewModelScope),
                    SampleVerticalSectionViewModel("E", scope = viewModelScope),
                    SampleVerticalSectionViewModel("F", scope = viewModelScope),
                    SampleVerticalSectionViewModel("G", scope = viewModelScope),
                )
            )
        }
    }

}