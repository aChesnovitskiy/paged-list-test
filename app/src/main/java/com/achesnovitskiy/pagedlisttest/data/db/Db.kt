package com.achesnovitskiy.pagedlisttest.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.achesnovitskiy.pagedlisttest.data.entites.Cat

@Database(
    entities = [Cat::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class Db : RoomDatabase() {

    abstract val catsDao: CatsDao
}