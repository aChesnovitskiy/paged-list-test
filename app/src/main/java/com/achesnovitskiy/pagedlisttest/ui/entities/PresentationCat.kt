package com.achesnovitskiy.pagedlisttest.ui.entities

data class PresentationCat(
    val id: String,
    val image_url: String,
    val isLoader: Boolean,
    val isError: Boolean
)

val loaderCat = PresentationCat(
    id = "-1",
    image_url = "",
    isLoader = true,
    isError = false
)

val errorCat = PresentationCat(
    id = "-2",
    image_url = "",
    isLoader = false,
    isError = true
)