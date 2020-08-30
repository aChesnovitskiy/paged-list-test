package com.achesnovitskiy.pagedlisttest.data.api

import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @Headers("x-api-key: 8a7c662e-c5e4-47d0-8f62-7306278663cf")
    @GET("favourites")
    fun getCats(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Observable<Response<List<Cat>>>

    @Headers("x-api-key: 8a7c662e-c5e4-47d0-8f62-7306278663cf")
    @DELETE("favourites/{favourite_id}")
    fun deleteCat(
        @Path("favourite_id") id: Int
    ): Completable
}