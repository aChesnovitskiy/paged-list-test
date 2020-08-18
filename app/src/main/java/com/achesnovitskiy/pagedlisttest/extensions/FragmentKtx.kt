package com.achesnovitskiy.pagedlisttest.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackbarWithAction(
    message: String,
    actionText: String,
    action: (View) -> Unit
) {
    Snackbar.make(
        this.requireView(),
        message,
        Snackbar.LENGTH_SHORT
    )
        .setAction(actionText, action)
        .show()
}