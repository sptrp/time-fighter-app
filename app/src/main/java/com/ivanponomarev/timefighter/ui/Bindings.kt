package com.ivanponomarev.timefighter.ui

import android.view.View
import androidx.databinding.BindingAdapter
import com.ivanponomarev.timefighter.R
import com.ivanponomarev.timefighter.base.longToast

object Bindings {

    @JvmStatic
    @BindingAdapter("android:onLongClick")
    fun onLongClick(
        view: View,
        func: () -> Unit
    ) {
        view.setOnLongClickListener {
            func()
            view.context.longToast(R.string.game_ready)
            return@setOnLongClickListener true
        }
    }
}