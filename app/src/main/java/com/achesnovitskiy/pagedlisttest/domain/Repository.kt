package com.achesnovitskiy.pagedlisttest.domain

import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.db.Db
import com.achesnovitskiy.pagedlisttest.domain.entities.DomainCat
import com.achesnovitskiy.pagedlisttest.extensions.toDomainCat
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

interface Repository {
    val catObservable: Observable<List<DomainCat>>

    var hasNextPage: Boolean

    fun refresh(): Completable

    fun loadNextPage(): Completable
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

    override var hasNextPage: Boolean = false

    private var nextPage: Int = 1

    override fun refresh(): Completable =
        api.getCats(0, CATS_ON_PAGE_LIMIT)
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

    override fun loadNextPage(): Completable =
        api.getCats(nextPage, CATS_ON_PAGE_LIMIT)
            .doOnNext { response ->
                db.catsDao.insertCats(response.body() ?: emptyList())

                nextPage++

                val paginationCount = response.headers().get("pagination-count")?.toInt() ?: 0

                hasNextPage = (paginationCount - nextPage * CATS_ON_PAGE_LIMIT) > 0
            }
            .ignoreElements()

    companion object {
        const val CATS_ON_PAGE_LIMIT = 10
    }
}