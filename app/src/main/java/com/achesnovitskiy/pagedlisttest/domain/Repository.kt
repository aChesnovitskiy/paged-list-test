package com.achesnovitskiy.pagedlisttest.domain

import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.db.Db
import com.achesnovitskiy.pagedlisttest.domain.entities.DomainCat
import com.achesnovitskiy.pagedlisttest.extensions.toDomainCat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface Repository {
    val catObservable: Observable<List<DomainCat>>

    val refreshCompletable: Completable

    val loadNextPageCompletable: Completable
}

class RepositoryImpl @Inject constructor(
    private val api: Api,
    private val db: Db
) : Repository {

    override val catObservable: Observable<List<DomainCat>>
        get() = db.catsDao.getCats()
            .map { dataCats ->
                dataCats.map { dataCat ->
                    dataCat.toDomainCat()
                }
            }

    override val refreshCompletable: Completable
        get() = api.getCats(0, CATS_ON_PAGE_LIMIT)
            .doOnNext { response ->
                db.runInTransaction {
                    db.catsDao.clearCats()
                    db.catsDao.insertCats(response.body() ?: emptyList())
                }

                nextPage = 1

                val paginationCount = response.headers().get("pagination-count")?.toInt() ?: 0

                hasNextPage = (paginationCount - nextPage * CATS_ON_PAGE_LIMIT) > 0
            }
            .ignoreElements()
            .subscribeOn(Schedulers.io())

    override val loadNextPageCompletable: Completable
        get() = api.getCats(nextPage, CATS_ON_PAGE_LIMIT)
            .doOnNext { response ->
                db.catsDao.insertCats(response.body() ?: emptyList())

                nextPage++

                val paginationCount = response.headers().get("pagination-count")?.toInt() ?: 0

                hasNextPage = (paginationCount - nextPage * CATS_ON_PAGE_LIMIT) > 0
            }
            .ignoreElements()
            .subscribeOn(Schedulers.io())

    private var hasNextPage: Boolean = false

    private var nextPage: Int = 1

    companion object {
        const val CATS_ON_PAGE_LIMIT = 10
    }
}