package com.achesnovitskiy.pagedlisttest.domain

import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.db.Db
import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import com.achesnovitskiy.pagedlisttest.domain.entities.DomainCat
import com.achesnovitskiy.pagedlisttest.extensions.toDomainCat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

interface Repository {
    val catObservable: Observable<List<DomainCat>>

    val hasNextPageObservable: Observable<Boolean>

    val refreshCompletable: Completable

    val loadNextPageCompletable: Completable

    fun deleteCatCompletable(id: Int): Completable
}

class RepositoryImpl @Inject constructor(
    private val api: Api,
    private val db: Db
) : Repository {

    private var hasNextPageBehaviorSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    private var nextPage: Int = 1

    override val catObservable: Observable<List<DomainCat>>
        get() = db.catsDao.getCats()
            .map { cats ->
                cats.map { cat ->
                    cat.toDomainCat()
                }
            }

    override val hasNextPageObservable: Observable<Boolean>
        get() = hasNextPageBehaviorSubject

    override val refreshCompletable: Completable
        get() = api.getCats(0, CATS_ON_PAGE_LIMIT)
            .doOnNext { response ->
                db.runInTransaction {
                    db.catsDao.clearCats()
                    db.catsDao.insertCats(response.body() as List<Cat>)
                }

                nextPage = 1

                val paginationCount = response.headers().get("pagination-count")?.toInt() ?: 0

                hasNextPageBehaviorSubject.onNext(
                    (paginationCount - nextPage * CATS_ON_PAGE_LIMIT) > 0
                )
            }
            .ignoreElements()
            .subscribeOn(Schedulers.io())

    override val loadNextPageCompletable: Completable
        get() = api.getCats(nextPage, CATS_ON_PAGE_LIMIT)
            .doOnNext { response ->
                db.catsDao.insertCats(response.body() as List<Cat>)

                nextPage++

                val paginationCount = response.headers().get("pagination-count")?.toInt() ?: 0

                hasNextPageBehaviorSubject.onNext(
                    (paginationCount - nextPage * CATS_ON_PAGE_LIMIT) > 0
                )
            }
            .ignoreElements()
            .subscribeOn(Schedulers.io())

    override fun deleteCatCompletable(id: Int): Completable = api.deleteCat(id)

    companion object {
        const val CATS_ON_PAGE_LIMIT = 7
    }
}