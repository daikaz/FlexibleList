package com.daikaz.ff.sample

import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleVerticalSectionViewModel(
    override val sectionID: String,
    override val scope: CoroutineScope,
    override val config: SectionConfiguration = SectionConfiguration()
) : SectionViewModel<Int>(sectionID, scope, config) {

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
        return 1
    }
}
