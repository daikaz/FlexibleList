package com.daikaz.ff.section

import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.utils.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class SectionViewModel<D>(
    open val sectionID: String,
    open val config: SectionConfiguration = SectionConfiguration()
) {

    private val _reloadFlow: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event(Unit))
    val reloadFlow: StateFlow<Event<Unit>> = _reloadFlow

    internal var hasAlreadySucceeded: Boolean? = null

    internal fun loadViewState(): Flow<Result<D>> {
        return loadBlockViewState()
    }

    abstract fun loadBlockViewState(): Flow<Result<D>>

    fun reload() {
        _reloadFlow.value = Event(Unit)
    }
}