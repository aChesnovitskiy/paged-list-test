package com.achesnovitskiy.pagedlisttest.data.api

import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import io.reactivex.Observable

class MockApi: Api {
    override fun getCats(): Observable<List<Cat>> =
        Observable.just(
            listOf(
                Cat(
                    id = "2026929",
                    image_url = "https://cdn2.thecatapi.com/images/MTcxMzAzOA.jpg"
                ),
                Cat(
                    id = "2026930",
                    image_url = "https://cdn2.thecatapi.com/images/47h.jpg"
                ),
                Cat(
                    id = "2026931",
                    image_url = "https://cdn2.thecatapi.com/images/ch7.jpg"
                ),
                Cat(
                    id = "2026932",
                    image_url = "https://cdn2.thecatapi.com/images/dpl.jpg"
                ),
                Cat(
                    id = "2026933",
                    image_url = "https://cdn2.thecatapi.com/images/eho.jpg"
                )
            )
        )
}