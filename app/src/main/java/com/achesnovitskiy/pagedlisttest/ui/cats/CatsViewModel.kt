package com.achesnovitskiy.pagedlisttest.ui.cats

import androidx.lifecycle.ViewModel
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.extensions.toPresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

interface CatsViewModel {

    val catObservable: Observable<List<PresentationCat>>

    val refreshObserver: Observer<Unit>
}

class CatsViewModelImpl @Inject constructor(private val repository: Repository) : ViewModel(),
    CatsViewModel {

    override val catObservable: Observable<List<PresentationCat>>
        get() = repository.catObservable
            .map { domainCats ->
                domainCats.map { domainCat ->
                    domainCat.toPresentationCat()
                }
            }

    override val refreshObserver: PublishSubject<Unit> = PublishSubject.create()

    init {
//        repository.refreshCats()
    }
}