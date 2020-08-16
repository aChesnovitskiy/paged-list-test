package com.achesnovitskiy.pagedlisttest.data.db

import androidx.room.TypeConverter
import com.achesnovitskiy.pagedlisttest.data.entites.CatImage

class Converters {

    @TypeConverter
    fun stringToCatImage(string: String): CatImage = CatImage(
        image_id = string.split(",")[0],
        image_url = string.split(",")[1]
    )

    @TypeConverter
    fun catImageToString(catImage: CatImage): String = catImage.image_id
        .plus(",")
        .plus(catImage.image_url)
}