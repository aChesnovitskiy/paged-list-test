package com.achesnovitskiy.pagedlisttest.ui.cats.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.achesnovitskiy.pagedlisttest.domain.Repository
import com.achesnovitskiy.pagedlisttest.ui.di.ViewScope
import com.achesnovitskiy.pagedlisttest.ui.cats.CatsViewModel
import com.achesnovitskiy.pagedlisttest.ui.cats.CatsViewModelImpl
import dagger.Module
import dagger.Provides

@Module
class CatsModule(
    private val viewModelStoreOwner: ViewModelStoreOwner
) {

    @Provides
    @ViewScope
    fun provideCatsViewModel(repository: Repository): CatsViewModel =
        ViewModelProvider(
            viewModelStoreOwner,
            CatsViewModelFactory(repository)
        ).get(CatsViewModelImpl::class.java)

    class CatsViewModelFactory(private val repository: Repository) :
        ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            CatsViewModelImpl(repository) as T
    }
}