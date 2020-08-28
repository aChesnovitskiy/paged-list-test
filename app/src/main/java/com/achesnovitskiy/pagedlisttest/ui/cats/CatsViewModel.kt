package com.achesnovitskiy.pagedlisttest.ui.cats

import android.os.Handler
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.extensions.toPresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

interface CatsViewModel {

    val catsAndHasNextPageObservable: Observable<Pair<List<PresentationCat>, Boolean>>

    val refreshingStateObservable: Observable<RefreshingState>

    val loadingNextPageStateObservable: Observable<LoadingState>

    val refreshObserver: Observer<Unit>

    val loadNextPageObserver: Observer<Unit>
}

class CatsViewModelImpl @Inject constructor(private val repository: Repository) : ViewModel(),
    CatsViewModel {

    private val refreshingStatePubishSubject: PublishSubject<RefreshingState> =
        PublishSubject.create()

    private val loadingNextPageStatePublishSubject: PublishSubject<LoadingState> =
        PublishSubject.create()

    override val catsAndHasNextPageObservable: Observable<Pair<List<PresentationCat>, Boolean>>
        get() = Observable
            .combineLatest(
                repository.catObservable
                    .map { domainCats ->
                        domainCats.map { domainCat ->
                            domainCat.toPresentationCat()
                        }
                    },
                repository.hasNextPageObservable,
                BiFunction { cats: List<PresentationCat>, hasNextPage: Boolean ->
                    cats to hasNextPage
                }
            )
            .subscribeOn(Schedulers.io())

    override val refreshingStateObservable: Observable<RefreshingState>
        get() = refreshingStatePubishSubject

    override val loadingNextPageStateObservable: Observable<LoadingState>
        get() = loadingNextPageStatePublishSubject

    override val refreshObserver: PublishSubject<Unit> = PublishSubject.create()

    override val loadNextPageObserver: PublishSubject<Unit> = PublishSubject.create()

    init {
        refreshObserver
            .switchMap {
                repository.refreshCompletable
                    .andThen(
                        Observable.just(
                            RefreshingState(
                                isRefreshing = false,
                                errorRes = null
                            )
                        )
                    )
                    .startWith(
                        RefreshingState(
                            isRefreshing = true,
                            errorRes = null
                        )
                    )
                    .onErrorReturnItem(
                        RefreshingState(
                            isRefreshing = false,
                            errorRes = R.string.msg_refreshing_error
                        )
                    )
            }
            .subscribeOn(Schedulers.io())
            .subscribe(refreshingStatePubishSubject)

        loadNextPageObserver
            .switchMap {
                repository.loadNextPageCompletable
                    .andThen(
                        Observable.just(
                            LoadingState(
                                isLoading = false,
                                errorRes = null
                            )
                        )
                    )
                    .startWith(
                        LoadingState(
                            isLoading = true,
                            errorRes = null
                        )
                    )
                    .onErrorReturnItem(
                        LoadingState(
                            isLoading = false,
                            errorRes = R.string.msg_loading_error
                        )
                    )
            }
            .subscribeOn(Schedulers.io())
            .subscribe(loadingNextPageStatePublishSubject)

        Handler().postDelayed(
            {
                refreshObserver.onNext(Unit)
            },
            100L
        )
    }
}

data class RefreshingState(
    val isRefreshing: Boolean,
    @StringRes val errorRes: Int?
)

data class LoadingState(
    val isLoading: Boolean,
    @StringRes val errorRes: Int?
)