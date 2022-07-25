package com.daikaz.ff

import java.io.Serializable

data class LoadViewState(
    val isInitial: Boolean = false,
    val isLoading: Boolean = false,
    val throwable: Throwable? = null,
) : Serializable {

    fun toInitial() = copy(isInitial = true, isLoading = false, throwable = null)
    fun toLoading() = copy(isInitial = false, isLoading = true, throwable = null)
    fun toSuccess() = copy(isInitial = false, isLoading = false, throwable = null)
    fun toFailure(throwable: Throwable?) = copy(isInitial = false, isLoading = false, throwable = throwable)
}

internal data class FlexibleListViewState<BusinessViewState>(
    val loadViewState: LoadViewState,
    val businessViewState: BusinessViewState
) : Serializable {

    val isInitial: Boolean get() = loadViewState.isInitial
    val isLoading: Boolean get() = loadViewState.isLoading
    val isFailure: Boolean get() = loadViewState.throwable != null
    val isSuccess: Boolean get() = !isInitial && !isLoading && !isFailure

    fun update(
        businessViewState: (() -> BusinessViewState)? = null,
        loadViewState: (() -> LoadViewState)? = null,
        isInitial: (() -> Boolean)? = null,
        isLoading: (() -> Boolean)? = null,
        throwable: (() -> Throwable?)? = null,
    ): FlexibleListViewState<BusinessViewState> {
        return if (businessViewState != null) {
            copy(businessViewState = businessViewState())
        } else {
            this
        }.run {
            if (loadViewState != null) {
                copy(loadViewState = loadViewState())
            } else {
                this
            }
        }.run {
            if (isInitial != null) {
                copy(loadViewState = this.loadViewState.copy(isInitial = isInitial()))
            } else {
                this
            }
        }.run {
            if (isLoading != null) {
                copy(loadViewState = this.loadViewState.copy(isLoading = isLoading()))
            } else {
                this
            }
        }.run {
            if (throwable != null) {
                copy(loadViewState = this.loadViewState.copy(throwable = throwable()))
            } else {
                this
            }
        }
    }
}
