package com.ivanponomarev.timefighter.ui

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.ivanponomarev.timefighter.BuildConfig
import com.ivanponomarev.timefighter.R

class AboutDialog : DialogFragment() {

    private val aboutMessage: Spanned by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(ABOUT, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(ABOUT)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.aboutTitle, BuildConfig.VERSION_NAME))
            .setMessage(aboutMessage)
            .create()

    override fun onStart() {
        super.onStart()
        dialog?.findViewById<TextView>(android.R.id.message)?.movementMethod =
            LinkMovementMethod.getInstance()
    }

    companion object {
        const val TAG = "about dialog"
        private const val ABOUT =
            "<a href=\"https://www.linkedin.com/in/ivan-ponomarev-38222018a\">Created by Ivan Ponomarev</a>"
    }
}