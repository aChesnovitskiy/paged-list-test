package com.achesnovitskiy.pagedlisttest.domain.di

import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.db.Db
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.domain.RepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepository(api: Api, db: Db): Repository = RepositoryImpl(api, db)
}