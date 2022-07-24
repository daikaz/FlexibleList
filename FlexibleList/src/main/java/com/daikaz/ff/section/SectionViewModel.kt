package com.daikaz.ff.section

import com.daikaz.ff.FlexibleListViewState
import com.daikaz.ff.LoadingViewState
import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

abstract class SectionViewModel<BusinessViewState>(
    open val sectionID: String,
    protected open val scope: CoroutineScope,
    open val config: SectionConfiguration = SectionConfiguration()
) {

    private val _reloadFlow: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event(Unit))
    val reloadFlow: StateFlow<Event<Unit>> = _reloadFlow

    private val _viewState: MutableStateFlow<FlexibleListViewState<BusinessViewState>> by lazy {
        MutableStateFlow(
            FlexibleListViewState(
                loadingViewState = initLoadViewState(),
                businessViewState = initBusinessViewState()
            )
        )
    }

    internal val viewState: StateFlow<FlexibleListViewState<BusinessViewState>> = _viewState

    internal fun trigger() {
        reloadFlow.flatMapLatest {
            flow {
                val currentViewState = viewState.value
                val newLoadingViewState = correctLoadViewState(currentViewState.loadingViewState, currentViewState.businessViewState)
                emit(currentViewState.copy(loadingViewState = newLoadingViewState))
                emitAll(loadBusinessViewState().mapLatest {
                    viewState.value.update(
                        viewState = { it },
                        loadingViewState = { viewState.value.loadingViewState.toSuccess() },
                    )
                })
            }.catch { e ->
                handlerError(viewState.value.businessViewState, viewState.value.loadingViewState, e).let { (loadViewState, newViewState) ->
                    _viewState.value = viewState.value.update(viewState = { newViewState }, loadingViewState = { loadViewState })
                }
            }
        }.onEach {
            _viewState.value = it
        }.flowOn(Dispatchers.IO).launchIn(scope)
    }

    private fun loadBusinessViewState(): Flow<BusinessViewState> {
        return loadBlockViewState()
    }

    abstract fun initBusinessViewState(): BusinessViewState

    open fun initLoadViewState(): LoadingViewState = LoadingViewState().toInitial()

    /**
     * Override it if you don't want to show loading state in a certain situation
     */
    open fun correctLoadViewState(loadingViewState: LoadingViewState, businessViewState: BusinessViewState) = loadingViewState.toLoading()

    open fun handlerError(
        businessViewState: BusinessViewState,
        loadingViewState: LoadingViewState,
        throwable: Throwable
    ): Pair<LoadingViewState, BusinessViewState> {
        return Pair(loadingViewState.toFailure(throwable = throwable), businessViewState)
    }

    abstract fun loadBlockViewState(): Flow<BusinessViewState>

    fun reload() {
        _reloadFlow.value = Event(Unit)
    }

    fun currentBusinessViewState() = viewState.value.businessViewState
    fun currentLoadViewState() = viewState.value.loadingViewState

    fun updateViewState(loadingViewState: LoadingViewState, businessViewState: BusinessViewState) {
        _viewState.value = viewState.value.copy(loadingViewState, businessViewState)
    }
}
