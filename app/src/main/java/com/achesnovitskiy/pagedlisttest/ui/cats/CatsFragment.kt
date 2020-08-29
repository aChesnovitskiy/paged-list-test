package com.achesnovitskiy.pagedlisttest.ui.cats

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.app.App.Companion.appComponent
import com.achesnovitskiy.pagedlisttest.ui.base.BaseFragment
import com.achesnovitskiy.pagedlisttest.ui.cats.di.CatsModule
import com.achesnovitskiy.pagedlisttest.ui.cats.di.DaggerCatsComponent
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.loaderCat
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_cats.*
import javax.inject.Inject

class CatsFragment : BaseFragment(R.layout.fragment_cats) {

    @Inject
    lateinit var catsViewModel: CatsViewModel

    private val catsAdapter: CatsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CatsAdapter {
            catsViewModel.loadNextPageObserver.onNext(Unit)
        }
    }

    private lateinit var snackbar: Snackbar

    private var isSnackbarInitialized: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerCatsComponent
            .builder()
            .appComponent(appComponent)
            .catsModule(
                CatsModule(viewModelStoreOwner = this)
            )
            .build()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(catsRecyclerView) {
            adapter = catsAdapter

            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }

        catsSwipeRefreshLayout.setOnRefreshListener {
            catsViewModel.refreshObserver.onNext(Unit)
        }
    }

    override fun onResume() {
        super.onResume()

        disposable = CompositeDisposable(
            catsViewModel.catsAndHasNextPageObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: Pair<List<PresentationCat>, Boolean> ->
                    val cats: MutableList<PresentationCat> = mutableListOf()

                    cats.addAll(it.first)

                    if (it.second) {
                        cats.add(loaderCat)
                    }

                    catsAdapter.updateCats(cats)
                },

            catsViewModel.refreshingStateObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { refreshingState ->
                    catsSwipeRefreshLayout.isRefreshing = refreshingState.isRefreshing

                    if (refreshingState.errorRes != null) {
                        catsAdapter.hideLoader()

                        showSnackbar(getString(refreshingState.errorRes))
                    } else {
                        dismissSnackbar()
                    }
                },

            catsViewModel.loadingNextPageStateObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { loadingState ->
                    if (loadingState.isLoading) {
//                        catsAdapter.showLoader()
                    } else {
                        catsAdapter.hideLoader()
                    }

                    if (loadingState.errorRes != null) {
                        showSnackbar(getString(loadingState.errorRes))
                    } else {
                        dismissSnackbar()
                    }
                }
        )
    }

    private fun showSnackbar(text: String) {
        snackbar = Snackbar.make(
            requireView(),
            text,
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.action_repeat)) {
                catsViewModel.refreshObserver.onNext(Unit)
            }
            show()
        }

        isSnackbarInitialized = true
    }

    private fun dismissSnackbar() {
        if (isSnackbarInitialized) {
            snackbar.dismiss()

            isSnackbarInitialized = false
        }
    }
}