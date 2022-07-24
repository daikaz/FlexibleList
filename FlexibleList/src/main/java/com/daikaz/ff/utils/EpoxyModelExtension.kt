package com.daikaz.ff.utils

import com.airbnb.epoxy.EpoxyModel

fun <T> EpoxyModel<T>.maxSpanCount(): EpoxyModel<T> {
    return spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
}

fun <T> EpoxyModel<T>.overrideSpanCount(spanCount: Int): EpoxyModel<T> {
    return spanSizeOverride { _, _, _ -> spanCount }
}

fun <T> EpoxyModel<T>.overrideSpanCount(ratio: Float): EpoxyModel<T> {
    return spanSizeOverride { totalSpanCount, _, _ -> (ratio * totalSpanCount).toInt() }
}
