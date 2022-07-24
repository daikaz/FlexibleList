package com.daikaz.ff.sample

import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleSameSizeGridSectionViewModel(override val sectionID: String) : SectionViewModel<Int>(sectionID) {

    override fun loadBlockViewState(): Flow<Result<Int>> {
        return flow {
            when (sectionID) {
                "B"  -> delay(3500)
                else -> delay(4000)
            }
            emit(Result.success(12))
        }
    }
}