package com.daikaz.ff

import java.io.Serializable

//interface ViewState

sealed class LoadViewState<ViewState>(open val viewState: ViewState? = null) {

    val isInitial: Boolean get() = this is Initial
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    data class Initial<ViewState>(override val viewState: ViewState) : LoadViewState<ViewState>(viewState)
    data class Loading<ViewState>(override val viewState: ViewState) : LoadViewState<ViewState>(viewState)
    data class Success<ViewState>(override val viewState: ViewState) : LoadViewState<ViewState>(viewState)
    data class Failure<ViewState>(val error: Throwable? = null, override val viewState: ViewState) : LoadViewState<ViewState>(viewState)
}

data class LoadingViewState(
    val isInitial: Boolean = false,
    val isLoading: Boolean = false,
    val throwable: Throwable? = null,
) : Serializable {

    fun toInitial() = copy(isInitial = true, isLoading = false, throwable = null)
    fun toLoading() = copy(isLoading = true, isInitial = false, throwable = null)
    fun toFailure(throwable: Throwable?) = copy(isLoading = false, isInitial = false, throwable = throwable)
    fun toSuccess() = copy(isLoading = false, isInitial = false, throwable = null)
}

internal data class FlexibleListViewState<BusinessViewState>(
    val loadingViewState: LoadingViewState,
    val businessViewState: BusinessViewState
) : Serializable {

    val isInitial: Boolean get() = loadingViewState.isInitial
    val isLoading: Boolean get() = loadingViewState.isLoading
    val isFailure: Boolean get() = loadingViewState.throwable != null
    val isSuccess: Boolean get() = !isInitial && !isLoading && !isFailure

    fun update(
        viewState: (() -> BusinessViewState)? = null,
        loadingViewState: (() -> LoadingViewState)? = null,
        isInitial: (() -> Boolean)? = null,
        isLoading: (() -> Boolean)? = null,
        throwable: (() -> Throwable?)? = null,
    ): FlexibleListViewState<BusinessViewState> {
        return if (viewState != null) {
            copy(businessViewState = viewState())
        } else {
            this
        }.run {
            if (loadingViewState != null) {
                copy(loadingViewState = loadingViewState())
            } else {
                this
            }
        }.run {
            if (isInitial != null) {
                copy(loadingViewState = this.loadingViewState.copy(isInitial = isInitial()))
            } else {
                this
            }
        }.run {
            if (isLoading != null) {
                copy(loadingViewState = this.loadingViewState.copy(isLoading = isLoading()))
            } else {
                this
            }
        }.run {
            if (throwable != null) {
                copy(loadingViewState = this.loadingViewState.copy(throwable = throwable()))
            } else {
                this
            }
        }
    }
}
