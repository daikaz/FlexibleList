package com.daikaz.ff.sample

import com.daikaz.ff.LoadViewState
import com.daikaz.ff.action.ReloadAction
import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleVerticalSectionViewModel(
    sectionID: String,
    scope: CoroutineScope,
    config: SectionConfiguration = SectionConfiguration()
) : SectionViewModel<Any, Any, Int>(sectionID, scope, config = config) {

    var i: Int = 0
    override fun loadBlockViewState(): Flow<Int> {
        return flow {
            when (sectionID) {
                "A"  -> delay(1000)
                "B"  -> delay(3500)
                "C"  -> delay(2000)
                "D"  -> delay(1500)
                "E"  -> delay(3000)
                else -> delay(4000)
            }
            if ("C" == sectionID && i < 2) {
                i++
                throw Exception("somehow error")
            } else {
                emit(6)
            }
        }
    }

    override fun initBusinessViewState(): Int {
        return 0
    }

    override fun correctLoadViewState(loadViewState: LoadViewState, businessViewState: Int): LoadViewState {
        if (businessViewState != 0) {
            return loadViewState
        }
        return super.correctLoadViewState(loadViewState, businessViewState)
    }

    override suspend fun intentToAction(intent: Any, loadViewState: LoadViewState, businessViewState: Int): Any {
        if (intent == 1) return object : ReloadAction {
        }
        return Unit
    }

    override suspend fun handleAction(action: Any, loadViewState: LoadViewState, businessViewState: Int): Pair<LoadViewState, Int> {
        return Pair(loadViewState, businessViewState)
    }
}
