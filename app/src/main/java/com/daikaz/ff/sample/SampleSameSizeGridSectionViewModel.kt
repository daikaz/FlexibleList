package com.daikaz.ff.sample

import com.daikaz.ff.LoadViewState
import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleSameSizeGridSectionViewModel(
    sectionID: String,
    scope: CoroutineScope,
) : SectionViewModel<Any, Any, Int>(sectionID, scope) {

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

    override fun correctLoadViewState(loadViewState: LoadViewState, businessViewState: Int): LoadViewState {
        if (businessViewState != 0) {
            return loadViewState
        }
        return super.correctLoadViewState(loadViewState, businessViewState)
    }

    override suspend fun intentToAction(intent: Any, loadViewState: LoadViewState, businessViewState: Int) = Unit

    override suspend fun handleAction(action: Any, loadViewState: LoadViewState, businessViewState: Int): Pair<LoadViewState, Int> {
        return Pair(loadViewState, businessViewState)
    }
}
