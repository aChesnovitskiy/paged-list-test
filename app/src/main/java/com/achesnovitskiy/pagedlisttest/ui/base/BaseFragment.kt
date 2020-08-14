package com.achesnovitskiy.pagedlisttest.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

abstract class BaseFragment(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {

    open var disposable: Disposable = Disposables.disposed()

    override fun onPause() {
        disposable.dispose()

        super.onPause()
    }

    override fun onDestroy() {
        disposable.dispose()

        super.onDestroy()
    }
}