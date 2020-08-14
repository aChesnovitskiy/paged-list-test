package com.achesnovitskiy.pagedlisttest.ui.cats.di

import com.achesnovitskiy.pagedlisttest.app.di.AppComponent
import com.achesnovitskiy.pagedlisttest.ui.di.ViewScope
import com.achesnovitskiy.pagedlisttest.ui.cats.CatsFragment
import dagger.Component

@ViewScope
@Component(dependencies = [AppComponent::class], modules = [CatsModule::class])
interface CatsComponent {

    fun inject(fragment: CatsFragment)
}