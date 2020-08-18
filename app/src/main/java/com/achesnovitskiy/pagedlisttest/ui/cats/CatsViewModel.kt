package com.achesnovitskiy.pagedlisttest.ui.cats

import androidx.lifecycle.ViewModel
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.extensions.toPresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

interface CatsViewModel {

    val catsObservable: Observable<List<PresentationCat>>

    val isLoadingObservable: Observable<Boolean>

    val refreshErrorObservable: Observable<Unit>

    val refreshObserver: Observer<Unit>

    val loadNextPageObserver: Observer<Unit>
}

class CatsViewModelImpl @Inject constructor(private val repository: Repository) : ViewModel(),
    CatsViewModel {

    private var disposable: Disposable = Disposables.disposed()

    override val catsObservable: Observable<List<PresentationCat>>
        get() = repository.catObservable
            .map { domainCats ->
                domainCats.map { domainCat ->
                    domainCat.toPresentationCat()
                }
            }

    override val isLoadingObservable: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    override val refreshErrorObservable: BehaviorSubject<Unit> = BehaviorSubject.create()

    override val refreshObserver: PublishSubject<Unit> = PublishSubject.create()

    override val loadNextPageObserver: PublishSubject<Unit> = PublishSubject.create()

    init {
        disposable = CompositeDisposable(
            refreshObserver
                .subscribe
                {
                    isLoadingObservable.onNext(true)

                    (disposable as CompositeDisposable).add(
                        repository.refresh()
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                {
                                    isLoadingObservable.onNext(false)
                                },
                                {
                                    isLoadingObservable.onNext(false)

                                    refreshErrorObservable.onNext(Unit)
                                }
                            )
                    )
                }
        )

        refreshObserver.onNext(Unit)
    }

    override fun onCleared() {
        disposable.dispose()
    }
}