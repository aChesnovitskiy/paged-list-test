package com.achesnovitskiy.pagedlisttest.data.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Cat(
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: CatImage
)

data class CatImage(
    @SerializedName("id")
    val image_id: String,
    @SerializedName("url")
    val image_url: String
)