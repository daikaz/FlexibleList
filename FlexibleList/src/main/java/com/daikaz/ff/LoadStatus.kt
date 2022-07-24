package com.daikaz.ff

internal enum class LoadStatus {
    SUCCESS, LOADING, ERROR;

    val isSuccess: Boolean get() = this == SUCCESS
    val isLoading: Boolean get() = this == LOADING
    val isFailure: Boolean get() = this == ERROR
}
