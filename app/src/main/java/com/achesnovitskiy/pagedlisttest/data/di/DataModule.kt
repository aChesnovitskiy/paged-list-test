package com.achesnovitskiy.pagedlisttest.data.di

import android.content.Context
import androidx.room.Room
import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.api.MockApi
import com.achesnovitskiy.pagedlisttest.data.db.Db
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {
    @Provides
    @Singleton
    fun provideApi(): Api = MockApi()

    @Provides
    @Singleton
    fun provideDb(context: Context): Db = Room.databaseBuilder(
        context,
        Db::class.java,
        DATABASE_NAME
    ).build()

    companion object {
        const val DATABASE_NAME = "database.db"
    }
}