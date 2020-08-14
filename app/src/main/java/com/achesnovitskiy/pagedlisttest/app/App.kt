package com.achesnovitskiy.pagedlisttest.app

import android.app.Application
import com.achesnovitskiy.pagedlisttest.app.di.AppComponent
import com.achesnovitskiy.pagedlisttest.app.di.AppModule
import com.achesnovitskiy.pagedlisttest.app.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(
                AppModule(context = this)
            )
            .build()
    }
}