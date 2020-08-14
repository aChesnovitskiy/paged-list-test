package com.achesnovitskiy.pagedlisttest.data.api

import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import io.reactivex.Observable

interface Api {

    fun getCats(): Observable<List<Cat>>
}