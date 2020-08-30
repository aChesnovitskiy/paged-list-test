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
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

interface CatsViewModel {

    val catsObservable: Observable<List<PresentationCat>>

    val selectedCatsObservable: Observable<List<PresentationCat>>

    val hasNextPageObservable: Observable<Boolean>

    val refreshingStateObservable: Observable<RefreshingState>

    val loadingNextPageStateObservable: Observable<LoadingState>

    val refreshObserver: Observer<Unit>

    val loadNextPageObserver: Observer<Unit>

    val toggleCatSelectionObserver: Observer<PresentationCat>

    val clearSelectedCatsObserver: Observer<Unit>
}

class CatsViewModelImpl @Inject constructor(private val repository: Repository) : ViewModel(),
    CatsViewModel {

    private val selectedCats: MutableList<PresentationCat> = mutableListOf()

    private val selectedCatsBehaviorSubject: BehaviorSubject<List<PresentationCat>> =
        BehaviorSubject.createDefault(emptyList())

    private val refreshingStatePublishSubject: PublishSubject<RefreshingState> =
        PublishSubject.create()

    private val loadingNextPageStatePublishSubject: PublishSubject<LoadingState> =
        PublishSubject.create()

    override val catsObservable: Observable<List<PresentationCat>>
        get() = repository.catObservable
            .map { domainCats ->
                domainCats.map { domainCat ->
                    domainCat.toPresentationCat()
                }
            }
            .subscribeOn(Schedulers.io())

    override val selectedCatsObservable: Observable<List<PresentationCat>>
        get() = selectedCatsBehaviorSubject

    override val hasNextPageObservable: Observable<Boolean>
        get() = repository.hasNextPageObservable

    override val refreshingStateObservable: Observable<RefreshingState>
        get() = refreshingStatePublishSubject

    override val loadingNextPageStateObservable: Observable<LoadingState>
        get() = loadingNextPageStatePublishSubject

    override val refreshObserver: PublishSubject<Unit> = PublishSubject.create()

    override val loadNextPageObserver: PublishSubject<Unit> = PublishSubject.create()

    override val toggleCatSelectionObserver: PublishSubject<PresentationCat> =
        PublishSubject.create()

    override val clearSelectedCatsObserver: PublishSubject<Unit> = PublishSubject.create()

    init {
        toggleCatSelectionObserver
            .map { cat ->
                when (cat.isSelected) {
                    true -> {
                        if (selectedCats.contains(cat)) {
                            selectedCats.remove(cat)
                        }
                    }

                    false -> selectedCats.add(
                        cat.copy(isSelected = true)
                    )
                }

                selectedCats
            }
            .subscribeOn(Schedulers.io())
            .subscribe(selectedCatsBehaviorSubject)

        clearSelectedCatsObserver
            .map {
                selectedCats.clear()

                selectedCats
            }
            .subscribeOn(Schedulers.io())
            .subscribe(selectedCatsBehaviorSubject)

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
            .subscribe(refreshingStatePublishSubject)

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