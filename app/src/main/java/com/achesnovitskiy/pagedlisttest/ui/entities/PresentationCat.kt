package com.achesnovitskiy.pagedlisttest.ui.entities

data class PresentationCat(
    val id: String,
    val image_url: String,
    val isLoader: Boolean = false,
    val isError: Boolean = false
)

val loaderCat = PresentationCat(
    id = "-1",
    image_url = "",
    isLoader = true
)