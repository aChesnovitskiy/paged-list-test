package com.achesnovitskiy.pagedlisttest.ui.entities

data class PresentationCat(
    val id: String,
    val image_url: String,
    val isSelected: Boolean,
    val isLoader: Boolean,
    val isError: Boolean
)

val loaderCat = PresentationCat(
    id = "-1",
    image_url = "",
    isSelected = false,
    isLoader = true,
    isError = false
)

val errorCat = PresentationCat(
    id = "-2",
    image_url = "",
    isSelected = false,
    isLoader = false,
    isError = true
)