package com.achesnovitskiy.pagedlisttest.extensions

import com.achesnovitskiy.pagedlisttest.data.entites.Cat
import com.achesnovitskiy.pagedlisttest.domain.entities.DomainCat
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat

fun Cat.toDomainCat(): DomainCat = DomainCat(
    id = this.id,
    image_url = this.image.image_url
)

fun DomainCat.toPresentationCat(): PresentationCat = PresentationCat(
    id = this.id,
    image_url = this.image_url,
    isLoader = false,
    isError = false
)