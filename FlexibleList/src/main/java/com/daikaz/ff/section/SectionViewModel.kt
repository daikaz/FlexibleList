package com.daikaz.ff.section

import androidx.annotation.WorkerThread
import com.daikaz.ff.FlexibleListViewState
import com.daikaz.ff.LoadViewState
import com.daikaz.ff.action.ReloadAction
import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.locks.Lock
import kotlin.coroutines.CoroutineContext

abstract class SectionViewModel<INTENT, ACTION, BusinessViewState> constructor(
    val sectionID: String,
    protected val defaultCoroutineScope: CoroutineScope,
    protected val defaultCoroutineContext: CoroutineContext = newSingleThreadContext(sectionID),
    val config: SectionConfiguration = SectionConfiguration(),
    protected val lock: Lock? = null
) {

    private val _reloadFlow: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event(Unit))
    private val reloadFlow: StateFlow<Event<Unit>> = _reloadFlow

    private val _viewState: MutableStateFlow<FlexibleListViewState<BusinessViewState>> by lazy {
        MutableStateFlow(
            FlexibleListViewState(
                loadViewState = initLoadViewState(),
                businessViewState = initBusinessViewState()
            )
        )
    }

    internal val viewState: StateFlow<FlexibleListViewState<BusinessViewState>> = _viewState

    init {
        reloadFlow.flatMapLatest {

            flow {

                with(viewState.value.copy()) {
                    val newLoadingViewState = correctLoadViewState(loadViewState, businessViewState)
                    if (newLoadingViewState != loadViewState) {
                        emit(copy(loadViewState = newLoadingViewState))
                    }
                }

                emitAll(loadBusinessViewState().mapLatest {
                    viewState.value.update(
                        businessViewState = { it },
                        loadViewState = { viewState.value.loadViewState.toSuccess() },
                    )
                })

            }.catchErrors()

        }.onEach {
            _viewState.value = it
        }.flowOn(defaultCoroutineContext).launchIn(defaultCoroutineScope)
    }

    private fun <T> Flow<T>.catchErrors(): Flow<T> = catch { throwable ->
        with(viewState.value.copy()) {
            handleErrors(loadViewState, businessViewState, throwable)
        }.run {
            val (loadViewState, newViewState) = this
            _viewState.value = viewState.value.update(businessViewState = { newViewState }, loadViewState = { loadViewState })
        }
    }

    /**
     * The only one way to update view state is dispatch new intent.
     * This is a close method.
     */
    fun dispatchIntent(intent: INTENT) = coroutineScopeOf(intent).launch(coroutineContextOf(intent), coroutineStartOf(intent)) {
        val intentHandler: suspend (intent: INTENT) -> Unit = intentHandler@{ intent ->
            // copy to prevent modifications
            with(viewState.value.copy()) {
                val action = intentToAction(intent, loadViewState, businessViewState)
                if (action is ReloadAction) {
                    reload()
                    return@intentHandler
                }

                val (newLoadingViewState, newBusinessViewState) = handleAction(action, loadViewState, businessViewState)

                if (loadViewState != newLoadingViewState) {
                    Pair(true, update(loadViewState = { newLoadingViewState }))
                } else {
                    Pair(false, this)
                }.run {
                    val (_, newViewState) = this
                    if (newViewState.businessViewState != newBusinessViewState) {
                        Pair(true, second.update(businessViewState = { newBusinessViewState }))
                    } else {
                        this
                    }
                }
            }.run {
                val (hasChanged, newViewState) = this
                if (!hasChanged) {
                    return@run
                }
                _viewState.value = newViewState
            }
        }

        if (lock == null) {
            intentHandler(intent)
            return@launch
        }

        if (lock.tryLock()) {
            try {
                intentHandler(intent)
            } finally {
                lock.unlock()
            }
        } else {
            intentHandler(intent)
        }
    }

    open fun coroutineContextOf(intent: INTENT) = defaultCoroutineContext
    open fun coroutineStartOf(intent: INTENT) = CoroutineStart.DEFAULT
    open fun coroutineScopeOf(intent: INTENT) = defaultCoroutineScope

    @WorkerThread
    abstract suspend fun intentToAction(intent: INTENT, loadViewState: LoadViewState, businessViewState: BusinessViewState): ACTION

    @WorkerThread
    abstract suspend fun handleAction(
        action: ACTION,
        loadViewState: LoadViewState,
        businessViewState: BusinessViewState
    ): Pair<LoadViewState, BusinessViewState>

    private fun loadBusinessViewState(): Flow<BusinessViewState> {
        return loadBlockViewState()
    }

    abstract fun loadBlockViewState(): Flow<BusinessViewState>

    /**
     * should not run on worker thread.
     */
    abstract fun initBusinessViewState(): BusinessViewState

    /**
     * should not run on worker thread.
     */
    open fun initLoadViewState(): LoadViewState = LoadViewState().toInitial()

    /**
     * Override it if you don't want to show loading state in a certain situation
     */
    open fun correctLoadViewState(loadViewState: LoadViewState, businessViewState: BusinessViewState) = loadViewState.toLoading()

    open fun handleErrors(
        loadViewState: LoadViewState,
        businessViewState: BusinessViewState,
        throwable: Throwable
    ): Pair<LoadViewState, BusinessViewState> {
        return Pair(loadViewState.toFailure(throwable = throwable), businessViewState)
    }

    fun currentBusinessViewState() = viewState.value.copy().businessViewState
    fun currentLoadViewState() = viewState.value.copy().loadViewState

    private fun reload() {
        _reloadFlow.value = Event(Unit)
    }
}
