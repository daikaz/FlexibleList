package com.daikaz.ff

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.daikaz.ff.configs.LayoutConfiguration
import com.daikaz.ff.section.SectionView
import com.daikaz.ff.section.SectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The FlexibleListFragment is a fragment to build a complex list. It is built on top of android epoxy library.
 *
 * The layout of FlexibleList is show as below layers:
 *      ┌───────────────────────────┐
 *      │       root view           │  ⤹ SwipeRefreshLayout which hosts a EpoxyRecyclerView inside
 *      │  ┌───────────────────────────┐
 *      │  │     SwipeRefreshLayout    │
 *      │  │  ┌───────────────────────────┐
 *      │  │  │    Empty State Layout     │
 *      │  │  │  ┌───────────────────────────┐
 *      │  │  │  │    Error State Layout     │
 *      │  │  │  │                           │
 *      │  │  │  │ ╔═══════════════════════╗ │
 *      │  │  │  │ ║  Message shows that   ║ │
 *      └──│  │  │ ║  there is something   ║ │
 *         │  │  │ ║     wrong happened.   ║ │
 *         └──│  │ ║      ┌─────────┐      ║ │
 *            │  │ ║      │ Reload  │      ║ │
 *            └──│ ║      └─────────┘      ║ │
 *               │ ╚═══════════════════════╝ │
 *               └───────────────────────────┘
 *
 *  The UI components include:
 *  - EpoxyRecyclerView: require, provide by override method `recyclerView()`
 *  - SwipeRefreshLayout: optional, provide by override method `swipeRefreshLayout()`
 *  - Empty view: optional, provide by override method `emptyView()`
 *  - Reload button: optional, provide by override method `reloadButton()` to reload when FlexibleList faces with error
 *  - Error view: optional, provide by override method `errorViewGroup()`. Error view group should wrap reload button
 *
 * An overview states of FlexibleList is shown as below:
 *
 *           Loading State                       Success State                     Empty state                       Error State
 *
 *     ┌───────────────────────────┐     ┌───────────────────────────┐     ┌───────────────────────────┐     ┌───────────────────────────┐
 *     │ ......................... │     │ ......................... │     │                           │     │                           │
 *     │ ╠═══════════════════════╣ │     │ ╠═══════════════════════╣ │     │                           │     │                           │
 *     │ ║    render shimmer     ║ │     │ ║     render actual     ║ │     │                           │     │                           │
 *     │ ║      items of         ║ │     │ ║     items' UI of      ║ │     │                           │     │                           │
 *     │ ║      section A        ║ │     │ ║      section A        ║ │     │                           │     │                           │
 *     │ ╠═══════════════════════╣ │     │ ╠═══════════════════════╣ │     │                           │     │ ╔═══════════════════════╗ │
 *     │ ║ render shimmer items  ║ │     │ ║ render actual items'  ║ │     │ ╔═══════════════════════╗ │     │ ║   Message show that   ║ │
 *     │ ║     of section B      ║ │     │ ║   UI of section B     ║ │     │ ║        Display        ║ │     │ ║  there is something   ║ │
 *     │ ╠═══════════════════════╣ │     │ ╠═══════════════════════╣ │     │ ║     Empty state UI    ║ │     │ ║     wrong happened.   ║ │
 *     │ ║                       ║ │     │ ║                       ║ │     │ ╚═══════════════════════╝ │     │ ║      ┌─────────┐      ║ │
 *     │ ......................... │     │ ......................... │     │                           │     │ ║      │ Reload  │      ║ │
 *     │ ║                       ║ │     │ ║                       ║ │     │                           │     │ ║      └─────────┘      ║ │
 *     │ ╠═══════════════════════╣ │     │ ╠═══════════════════════╣ │     │                           │     │ ╚═══════════════════════╝ │
 *     │ ║ render shimmer items  ║ │     │ ║ render actual items'  ║ │     │                           │     │                           │
 *     │ ║    of nTh section     ║ │     │ ║   UI of nTh section   ║ │     │                           │     │                           │
 *     │ ╠═══════════════════════╣ │     │ ╠═══════════════════════╣ │     │                           │     │                           │
 *     │ ......................... │     │ ......................... │     │                           │     │                           │
 *     └───────────────────────────┘     └───────────────────────────┘     └───────────────────────────┘     └───────────────────────────┘
 *                            ↖              ↗                                          ↑                                   ↑
 *                           EpoxyRecyclerView                                    any ViewGroup                       any ViewGroup
 * A section has its own:
 * - ViewModel with mechanism to load data
 * - Loading (optional) | Error (optional) | Success (require) UI
 * - Error handler
 * - Item decorator
 */
abstract class FlexibleListFragment<BD, VM : FlexibleListViewModel> : Fragment() where BD : ViewBinding {

    private var _binding: BD? = null

    private val sectionViewWithDecors = linkedMapOf<String, RecyclerView.ItemDecoration>()

    abstract val viewModel: VM

    open val controller: FlexibleListAsyncEpoxyController = FlexibleListAsyncEpoxyController()

    open val flowOnContext = newSingleThreadContext("FXLThread")

    private var job: Job? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!

    // Do not move it to ViewModel,
    private val sectionViews = CopyOnWriteArrayList<SectionView<*>>()

    private val modelBuildFinishedListener = OnModelBuildFinishedListener {
        emptyView()?.isVisible = controller.numberOfModels == 0
        if (controller.numberOfModels == 0) {
            swipeRefreshLayout()?.isVisible = false
            errorViewGroup()?.isVisible = false
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = inflateViewBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): BD
    open fun reloadButton(): View? = null
    open fun swipeRefreshLayout(): SwipeRefreshLayout? = null
    open fun errorViewGroup(): View? = null
    open fun emptyView(): View? = null
    abstract fun recyclerView(): EpoxyRecyclerView

    open val layoutConfiguration by lazy {
        LayoutConfiguration.defaultGridLayoutConfig(binding.root.context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewModel()
    }

    private fun setupViews() {
        reloadButton()?.setOnClickListener {
            reload()
        }

        swipeRefreshLayout()?.setOnRefreshListener {
            reload()
        }

        controller.addModelBuildListener(modelBuildFinishedListener)

        with(layoutConfiguration) {
            controller.spanCount = totalSpanCount
            if (layoutManager is GridLayoutManager) {
                layoutManager.spanSizeLookup = controller.spanSizeLookup
            }
            recyclerView().layoutManager = layoutManager
            recyclerView().setController(controller)
            controller.isDebugLoggingEnabled = BuildConfig.DEBUG
        }

    }

    private fun setupViewModel() {
        job = viewModel.sectionViewModels.onEach {
            //lifecycleScope.launch(Dispatchers.Main) {
            //    // TODO: try to disable animation
            //    recyclerView().itemAnimator = null
            //}
            controller.submitSectionIDs(viewModel.sectionWithLoadStatus.map { it.sectionID })
        }.flatMapLatest { event ->
            with(event.getContentIfNotHandled()) {
                val result = this?.mapNotNull { sectionViewModel -> getSectionViewFrom(sectionViewModel) } ?: emptyList()
                flow {
                    emit(result)
                }
            }.onEach {
                it.forEach { view ->
                    view.viewModel.trigger()
                }
                sectionViews.clear()
                sectionViews.addAll(it)
            }.flatMapLatest {
                it.map { view ->
                    view.loadDataThenConvertToEpoxyModels()
                }.merge().handleErrors()
            }
            //with(event.getContentIfNotHandled()) {
            //    this?.mapNotNull { sectionViewModel -> getSectionViewFrom(sectionViewModel) } ?: emptyList()
            //}.map { view ->
            //    view.loadDataThenConvertToEpoxyModels()
            //}.merge().handleErrors()
        }.updateUI().onCompletion { e ->
            Log.d("FlexibleList", "Unexpected completion with error: $e")
        }.flowOn(flowOnContext).launchIn(lifecycleScope)
    }

    private fun <D> SectionView<D>.loadDataThenConvertToEpoxyModels(): Flow<Present<D>> {
        return viewModel.viewState.
            //flatMapLatest {
            //    viewModel.loadBusinessViewState()
            //}
        onEach updateSpanCount@{
            totalSpanCount = layoutConfiguration.totalSpanCount
            containerSize = with(recyclerView()) {
                Size(width - paddingStart - paddingEnd, height - paddingTop - paddingBottom)
            }
        }.mapLatest { dataHasStatus ->
            val result = dataHasStatus.businessViewState

            if (dataHasStatus.isLoading) {
                return@mapLatest Present(this, LoadStatus.LOADING, buildEpoxyModels(LoadStatus.LOADING))
            }

            if (dataHasStatus.isSuccess) {
                return@mapLatest Present(this, LoadStatus.SUCCESS, buildEpoxyModels(LoadStatus.SUCCESS, result))
            }

            return@mapLatest when {
                viewModel.config.errorHandleType.isCorrupt -> throw CorruptException(viewModel.sectionID)
                viewModel.config.errorHandleType.isShow    -> Present(this, LoadStatus.ERROR, buildEpoxyModels(LoadStatus.ERROR))
                else                                       -> Present(this, LoadStatus.ERROR, emptyList())
            }
        }
    }

    /**
     * This is default error handler, you should override it for your own app
     * For example: show Toast, Snackbar over the main list instead of hide them
     */
    open fun <T> Flow<T>.handleErrors(): Flow<T> = catch { e ->
        if (swipeRefreshLayout()?.isVisible == true && e is CorruptException) {
            lifecycleScope.launch(Dispatchers.Main) {
                errorViewGroup()?.isVisible = true
                swipeRefreshLayout()?.isVisible = false
            }
        }
    }

    private fun Flow<Present<*>>.updateUI(): Flow<Present<*>> = onEach { (sectionView, status, models) ->
        with(sectionView.viewModel) dispatchChanged@{
            lifecycleScope.launch(Dispatchers.Main) updateItemDecoration@{
                sectionViewWithDecors[sectionID]?.run {
                    recyclerView().removeItemDecoration(this)
                }
                with(sectionView.itemDecoration) {
                    sectionViewWithDecors[sectionID] = this
                    recyclerView().addItemDecoration(this)
                }
            }

            // TODO: UnitTest below lines
            when {
                status.isSuccess -> {
                    viewModel.updateSuccess(sectionID, status = LoadStatus.SUCCESS)
                    controller.submitModels(status, sectionID, models, viewModel.hasSuccessfullyLoadAllCorruptSections())

                }
                status.isLoading -> {
                    val mode = config.loadingMode
                    if (mode.isNone) return@dispatchChanged
                    val hasAlreadySucceeded = viewModel.sectionWithLoadStatus.firstOrNull { it.sectionID == sectionID }?.loadStatus
                    if (
                        mode.isAlways
                        or (mode.isFirstTimeOnly && hasAlreadySucceeded?.isLoading == true)
                        or (mode.isUntilSuccess && hasAlreadySucceeded?.isSuccess != true)
                        or (viewModel.hasSuccessfullyLoadAllCorruptSections()).not()
                    ) {
                        controller.submitModels(status, sectionID, models)
                    }
                }
                status.isFailure -> {
                    viewModel.updateSuccess(sectionID, status = LoadStatus.ERROR)
                    controller.submitModels(status, sectionID, models, viewModel.hasSuccessfullyLoadAllCorruptSections())
                }
                else             -> {
                }
            }

            sectionViews.forEach {
                val (start, end) = controller.findStartAndEndIndexOf(it.viewModel.sectionID)
                it.startIndex = start
                it.endIndex = end
            }
        }.run {
            if (swipeRefreshLayout()?.isVisible != true) {
                lifecycleScope.launch(Dispatchers.Main) {
                    errorViewGroup()?.isVisible = false
                    swipeRefreshLayout()?.isVisible = true
                }
            }
        }
    }

    abstract fun getSectionViewFrom(viewModel: SectionViewModel<*>): SectionView<*>?

    private fun reload() {
        with(sectionViewWithDecors) {
            forEach { (_, itemDecoration) ->
                recyclerView().removeItemDecoration(itemDecoration)
            }
        }.run {
            viewModel.reload()
            swipeRefreshLayout()?.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        controller.removeModelBuildListener(modelBuildFinishedListener)
        super.onDestroyView()
        _binding = null
    }

    private data class Present<D>(
        val view: SectionView<D>,
        val status: LoadStatus,
        val models: List<EpoxyModel<*>>,
    )

    private class CorruptException(sectionID: String) : Exception("Section $sectionID is required")

}
