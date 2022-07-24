package com.daikaz.ff.sample

import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleSameSizeGridSectionViewModel(
    override val sectionID: String,
    override val scope: CoroutineScope,
) : SectionViewModel<Int>(sectionID, scope) {

    override fun loadBlockViewState(): Flow<Int> {
        return flow {
            when (sectionID) {
                "B"  -> delay(3500)
                else -> delay(4000)
            }
            emit(12)
        }
    }

    override fun initBusinessViewState(): Int = 0
}