package com.daikaz.ff.configs

data class SectionConfiguration(
    val errorHandleType: ErrorHandleType = ErrorHandleType.HIDE,
    val loadingMode: LoadingMode = LoadingMode.UNTIL_SUCCESS
) {

    enum class ErrorHandleType {
        CORRUPT, SHOW, HIDE;

        val isCorrupt get() = this == CORRUPT
        val isShow get() = this == SHOW
    }

    enum class LoadingMode {
        ALWAYS, UNTIL_SUCCESS, FIRST_TIME_ONLY, NONE;

        val isAlways: Boolean get() = this == ALWAYS
        val isNone: Boolean get() = this == NONE
        val isUntilSuccess: Boolean get() = this == UNTIL_SUCCESS
        val isFirstTimeOnly: Boolean get() = this == FIRST_TIME_ONLY
    }
}

