package com.daikaz.ff

import android.util.Log
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListDebuggableEpoxyModel
import com.daikaz.ff.epoxy.model.FlexibleListHeaderEpoxyModel
import java.lang.Integer.max
import java.util.concurrent.CopyOnWriteArrayList

class FlexibleListAsyncEpoxyController : AsyncEpoxyController() {

    private val lock = Any()

    private val sectionIDsInOrder = CopyOnWriteArrayList<String>()

    private val sectionWithModels = CopyOnWriteArrayList<SectionWithModels>()
    private val sectionWithShimmers = CopyOnWriteArrayList<SectionWithModels>()
    private val sectionWithErrors = CopyOnWriteArrayList<SectionWithModels>()

    @Volatile
    var numberOfModels: Int = 0
        private set

    fun submitSectionIDs(ids: List<String>) {
        sectionIDsInOrder.clear()
        sectionIDsInOrder.addAll(ids)
    }

    internal fun submitModels(status: LoadStatus, sectionID: String, models: List<EpoxyModel<*>>, thenShouldRebuildUI: Boolean = true) {
        when {
            status.isLoading -> sectionWithShimmers.addOrReplace(sectionID, models, thenShouldRebuildUI)
            status.isSuccess -> sectionWithModels.addOrReplace(sectionID, models, thenShouldRebuildUI)
            status.isFailure -> sectionWithErrors.addOrReplace(sectionID, models, thenShouldRebuildUI)
        }
    }

    override fun isStickyHeader(position: Int): Boolean {
        if (position < 0) return false
        return adapter.getModelAtPosition(position) is FlexibleListHeaderEpoxyModel
    }

    override fun buildModels() {
        with(sectionIDsInOrder) {
            map {
                extractLatestModelsBySectionID(it)
            }.flatten()
        }.forEach { model ->
            try {
                model.addTo(this)
            } catch (e: Exception) {
                if (model is FlexibleListDebuggableEpoxyModel) {
                    Log.e("FlexibleList", "${e.message} of ${model.debugName}")
                }
            }
        }.run {
            numberOfModels = super.getModelCountBuiltSoFar()
        }
    }

    fun findStartAndEndIndexOf(sectionID: String): Pair<Int, Int> {
        var startIndex = 0
        var noOfItems = extractLatestModelsBySectionID(sectionIDsInOrder[0]).size
        for (i in 0 until sectionIDsInOrder.size) {
            noOfItems = extractLatestModelsBySectionID(sectionIDsInOrder[i]).size
            if (sectionID == sectionIDsInOrder[i]) {
                break
            }
            startIndex += noOfItems
        }

        Log.e("seseseseti", "sectionID: $sectionID startIndex: $startIndex noOfItems: $noOfItems")
        return Pair(startIndex, startIndex + max(0, noOfItems -1))
    }

    private fun CopyOnWriteArrayList<SectionWithModels>.addOrReplace(sectionID: String, models: List<EpoxyModel<*>>, thenRebuildUI: Boolean) =
        synchronized(lock) {
            indexOfFirst { sectionID == it.sectionID }.run {
                val data = SectionWithModels(sectionID, models, System.currentTimeMillis())
                if (this != -1) {
                    removeAt(this)
                    add(this, data)
                } else {
                    add(data)
                }
            }

            if (thenRebuildUI) {
                EpoxyController.defaultModelBuildingHandler?.post {
                    requestModelBuild()
                }
            }
        }

    private fun extractLatestModelsBySectionID(sectionID: String): List<EpoxyModel<*>> = run {
        val model = sectionWithModels.firstOrNull { it.sectionID == sectionID }
        val shimmer = sectionWithShimmers.firstOrNull { it.sectionID == sectionID }
        val error = sectionWithErrors.firstOrNull { it.sectionID == sectionID }

        // extract added time
        val modelAddAt = model?.lastUpdate ?: 0L
        val shimmerAddAt = shimmer?.lastUpdate ?: 0L
        val errorAddAt = error?.lastUpdate ?: 0L

        // get the latest added time
        with(arrayOf(modelAddAt, shimmerAddAt, errorAddAt).maxOrNull() ?: System.currentTimeMillis()) {
            when (this) {
                modelAddAt   -> model?.models
                shimmerAddAt -> shimmer?.models
                errorAddAt   -> error?.models
                else         -> emptyList()
            } ?: emptyList()
        }
    }

    private data class SectionWithModels constructor(
        val sectionID: String,
        val models: List<EpoxyModel<*>>,
        val lastUpdate: Long = 0
    )
}
