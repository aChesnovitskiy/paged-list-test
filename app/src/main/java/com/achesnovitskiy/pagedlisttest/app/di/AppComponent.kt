package com.achesnovitskiy.pagedlisttest.app.di

import android.content.Context
import com.achesnovitskiy.pagedlisttest.data.api.Api
import com.achesnovitskiy.pagedlisttest.data.di.DataModule
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.domain.di.RepositoryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    val context: Context

    val repository: Repository

    val api: Api
}