package com.daikaz.ff

enum class SectionItemDisplayType {
    VERTICAL, CAROUSEL, GRID, SELF_LAYOUT
}

sealed class SectionDisplay {
    object Vertical : SectionDisplay()
    data class Grid(val noOfColumns: Int = 1) : SectionDisplay()
    object Carousel : SectionDisplay()
    object None : SectionDisplay()
}
