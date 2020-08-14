package com.achesnovitskiy.pagedlisttest.ui.cats

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.app.App.Companion.appComponent
import com.achesnovitskiy.pagedlisttest.ui.base.BaseFragment
import com.achesnovitskiy.pagedlisttest.ui.cats.di.DaggerCatsComponent
import com.achesnovitskiy.pagedlisttest.ui.cats.di.CatsModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cats.*
import javax.inject.Inject

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

        disposable = catsViewModel.catObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {cats ->
                catsAdapter.submitList(cats)
            }
    }
}