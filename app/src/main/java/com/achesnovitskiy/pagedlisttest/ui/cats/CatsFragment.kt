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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cats.*
import javax.inject.Inject

@ExperimentalStdlibApi
class CatsFragment : BaseFragment(R.layout.fragment_cats) {

    @Inject
    lateinit var catsViewModel: CatsViewModel

    private val catsAdapter: CatsAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CatsAdapter()
    }

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

            catsSwipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        disposable = CompositeDisposable(
            catsViewModel.catsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { cats ->
                        catsAdapter.submitList(cats)
                    },
                    {
                        // TODO
                    }
                ),

            catsViewModel.isLoadingObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { isLoading ->
                        if (isLoading) {
                            showLoader()
                        } else {
                            hideLoader()
                        }
                    },
                    {
                        // TODO
                    }
                )
        )
    }

    private fun showLoader() {
        if (catsAdapter.currentList.isNullOrEmpty() || !catsAdapter.currentList.last().isLoader) {
            val list: MutableList<PresentationCat> = mutableListOf()

            list.addAll(catsAdapter.currentList)

            list.add(
                PresentationCat(
                    id = "",
                    image_url = "",
                    isLoader = true
                )
            )

            catsAdapter.submitList(list)
        }
    }

    private fun hideLoader() {
        if (catsAdapter.currentList.last().isLoader) {
            val list: MutableList<PresentationCat> = mutableListOf()

            list.addAll(catsAdapter.currentList)

            list.removeLast()

            catsAdapter.submitList(list)
        }
    }
}