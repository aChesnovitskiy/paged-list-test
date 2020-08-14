package com.achesnovitskiy.pagedlisttest.data.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cat(
    @PrimaryKey val id: String,
    val image_url: String
)