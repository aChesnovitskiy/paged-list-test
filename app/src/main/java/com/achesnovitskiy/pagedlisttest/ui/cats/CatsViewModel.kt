package com.achesnovitskiy.pagedlisttest.ui.cats

import android.os.Handler
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.extensions.toPresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface CatsViewModel {

    val catsObservable: Observable<List<PresentationCat>>

    val selectedCatsObservable: Observable<List<PresentationCat>>

    val hasNextPageObservable: Observable<Boolean>

    val refreshingStateObservable: Observable<RefreshingState>

    val loadingNextPageStateObservable: Observable<LoadingState>

    val deletingSelectedCatsStateObservable: Observable<DeletingState>

    val refreshObserver: Observer<Unit>

    val loadNextPageObserver: Observer<Unit>

    val toggleCatSelectionObserver: Observer<PresentationCat>

    val clearSelectedCatsObserver: Observer<Unit>

    val deleteSelectedCatsObserver: Observer<Unit>
}

class CatsViewModelImpl @Inject constructor(private val repository: Repository) : ViewModel(),
    CatsViewModel {

    private val refreshingStatePublishSubject: PublishSubject<RefreshingState> =
        PublishSubject.create()

    private val loadingNextPageStatePublishSubject: PublishSubject<LoadingState> =
        PublishSubject.create()

    private val selectedCatsBehaviorSubject: BehaviorSubject<List<PresentationCat>> =
        BehaviorSubject.createDefault(emptyList())

    private val deletingSelectedCatsStatePublishSubject: BehaviorSubject<DeletingState> =
        BehaviorSubject.createDefault(
            DeletingState(
                isDeleting = false,
                isDeleted = false,
                errorRes = null
            )
        )

    private val selectedCats: MutableList<PresentationCat> = mutableListOf()

    private val deletingCats: MutableList<PresentationCat> = mutableListOf()

    override val catsObservable: Observable<List<PresentationCat>>
        get() = repository.catObservable
            .map { domainCats ->
                domainCats.map { domainCat ->
                    domainCat.toPresentationCat()
                }
            }
            .subscribeOn(Schedulers.io())

    override val hasNextPageObservable: Observable<Boolean>
        get() = repository.hasNextPageObservable

    override val refreshingStateObservable: Observable<RefreshingState>
        get() = refreshingStatePublishSubject

    override val loadingNextPageStateObservable: Observable<LoadingState>
        get() = loadingNextPageStatePublishSubject

    override val selectedCatsObservable: Observable<List<PresentationCat>>
        get() = selectedCatsBehaviorSubject

    override val deletingSelectedCatsStateObservable: Observable<DeletingState>
        get() = deletingSelectedCatsStatePublishSubject

    override val refreshObserver: PublishSubject<Unit> = PublishSubject.create()

    override val loadNextPageObserver: PublishSubject<Unit> = PublishSubject.create()

    override val toggleCatSelectionObserver: PublishSubject<PresentationCat> =
        PublishSubject.create()

    override val clearSelectedCatsObserver: PublishSubject<Unit> = PublishSubject.create()

    override val deleteSelectedCatsObserver: PublishSubject<Unit> = PublishSubject.create()

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

                deletingCats.clear()

                deletingCats.addAll(selectedCats)

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

        deleteSelectedCatsObserver
            .switchMap {
                val deletingCatsCount = 0

                selectedCatsBehaviorSubject.onNext(deletingCats)

//                repository.deleteCatCompletable(deletingCats[0].id.toInt())
                Completable.complete()
                    .delay(2000L, TimeUnit.MILLISECONDS)
                    .andThen(
                        Observable.just(
//                            DeletingState(
//                                isDeleting = false,
//                                isDeleted = true,
//                                errorRes = null
//                            )
                            DeletingState(
                                isDeleting = false,
                                isDeleted = false,
                                errorRes = R.string.msg_deleting_error
                            )
                        )
                    )
                    .startWith(
                        DeletingState(
                            isDeleting = true,
                            isDeleted = false,
                            errorRes = null
                        )
                    )
                    .onErrorReturnItem(
                        DeletingState(
                            isDeleting = false,
                            isDeleted = false,
                            errorRes = R.string.msg_deleting_error
                        )
                    )
            }
            .doOnComplete {
                deletingCats.clear()
            }
            .subscribeOn(Schedulers.io())
            .subscribe(deletingSelectedCatsStatePublishSubject)

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

data class DeletingState(
    val isDeleting: Boolean,
    val isDeleted: Boolean,
    @StringRes val errorRes: Int?
)