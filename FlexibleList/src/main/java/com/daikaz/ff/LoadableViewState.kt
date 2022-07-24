package com.daikaz.ff

interface ViewState

sealed class LoadableViewState<VS : ViewState>(open val viewState: VS? = null) {

    val isInitial: Boolean get() = this is Initial
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    data class Initial<VS : ViewState>(override val viewState: VS) : LoadableViewState<VS>(viewState)
    data class Loading<VS : ViewState>(override val viewState: VS) : LoadableViewState<VS>(viewState)
    data class Success<VS : ViewState>(override val viewState: VS) : LoadableViewState<VS>(viewState)
    data class Failure<VS : ViewState>(override val viewState: VS, val error: Throwable? = null) : LoadableViewState<VS>(viewState)
}
