package com.ivanponomarev.timefighter.base

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.longToast(@StringRes msgRes: Int, args: List<Any?> = emptyList()) = Toast.makeText(
    this,
    getString(msgRes, *args.toTypedArray()),
    Toast.LENGTH_LONG
).show()