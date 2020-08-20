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

    fun refresh(): Completable

    fun loadNextPage(page: Int): Completable
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

    override fun refresh(): Completable = api.getCats(0)
        .doOnNext { cats ->
            db.runInTransaction {
                db.catsDao.clearCats()
                db.catsDao.insertCats(cats)
            }
        }
        .ignoreElements()

    override fun loadNextPage(page: Int): Completable = api.getCats(page)
        .doOnNext { cats ->
            db.catsDao.insertCats(cats)
        }
        .ignoreElements()
}