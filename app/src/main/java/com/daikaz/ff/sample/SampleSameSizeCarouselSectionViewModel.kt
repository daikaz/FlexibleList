package com.daikaz.ff.sample

import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleSameSizeCarouselSectionViewModel(
    override val sectionID: String
) : SectionViewModel<Int>(sectionID) {

    override fun loadBlockViewState(): Flow<Result<Int>> {
        return flow {
            delay(2500)
            emit(Result.success(12))
        }
    }

}