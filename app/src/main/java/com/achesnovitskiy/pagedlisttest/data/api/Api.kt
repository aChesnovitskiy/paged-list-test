package com.achesnovitskiy.pagedlisttest.data.api

import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Api {

    @Headers("x-api-key: 8a7c662e-c5e4-47d0-8f62-7306278663cf")
    @GET("favourites?limit=5")
    fun getCats(@Query("page") page: Int): Observable<List<Cat>>
}