package com.daikaz.ff

import androidx.lifecycle.ViewModel
import com.daikaz.ff.configs.SectionConfiguration
import com.daikaz.ff.section.SectionViewModel
import com.daikaz.ff.utils.Event
import kotlinx.coroutines.flow.*
import java.util.concurrent.CopyOnWriteArrayList


abstract class FlexibleListViewModel : ViewModel() {

    private val _refreshFlow: MutableStateFlow<Event<Unit>> = MutableStateFlow(Event(Unit))
    private val refreshFlow: StateFlow<Event<Unit>> = _refreshFlow.asStateFlow()

    internal val sectionWithLoadStatus = CopyOnWriteArrayList<SectionWithSuccess>()

    internal val sectionViewModels = refreshFlow.flatMapLatest {
        provideSectionViewModels().mapLatest wrapThemUnderEvent@{
            Event(it)
        }.onEach consumeToDoSomeInit@{
            it.peek().map { viewModel ->
                SectionWithSuccess(viewModel.sectionID, viewModel.config.errorHandleType)
            }.run copyOldLoadInformationToNewListIfSectionHasAlreadyAddedToScreen@{
                forEach { n ->
                    n.loadStatus = sectionWithLoadStatus.firstOrNull { s ->
                        s.sectionID == n.sectionID
                    }?.loadStatus ?: LoadStatus.LOADING
                }.run {
                    sectionWithLoadStatus.clear()
                    sectionWithLoadStatus.addAll(this@copyOldLoadInformationToNewListIfSectionHasAlreadyAddedToScreen)
                }
            }
        }.catch someExceptionsMayOccurAndWeLetsApplicationHandleThem@{ e ->
            throw e
        }
    }

    abstract fun provideSectionViewModels(): Flow<List<SectionViewModel<*, * , *>>>

    internal fun updateSuccess(sectionID: String, status: LoadStatus) {
        sectionWithLoadStatus.firstOrNull { sectionID == it.sectionID }?.run {
            this.loadStatus = status
        }
    }

    internal fun hasSuccessfullyLoadAllCorruptSections(): Boolean = with(consistentContentMode()) {
        if (isWaitUntilCompleteLastCorruptSection) {
            return@with sectionWithLoadStatus.filter filterCorruptSections@{
                it.errorHandleType.isCorrupt
            }.firstOrNull getAnUnloadSection@{
                it.loadStatus == LoadStatus.LOADING
            }.run successIfThereIsNoSectionFound@{
                this == null
            }
        }
        return@with true
    }

    open fun consistentContentMode() = ContentConsistentMode.IMMEDIATELY

    fun reload() {
        _refreshFlow.value = Event(Unit)
    }

    enum class ContentConsistentMode {
        WAIT_UNTIL_COMPLETE_LAST_CORRUPT_SECTION, IMMEDIATELY;

        val isWaitUntilCompleteLastCorruptSection get() = this == WAIT_UNTIL_COMPLETE_LAST_CORRUPT_SECTION
    }

    internal data class SectionWithSuccess constructor(
        val sectionID: String,
        val errorHandleType: SectionConfiguration.ErrorHandleType,
        var loadStatus: LoadStatus = LoadStatus.LOADING,
    )
}

