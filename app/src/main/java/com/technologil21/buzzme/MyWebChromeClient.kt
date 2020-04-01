package com.technologil21.buzzme

import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog

internal class MyWebChromeClient : WebChromeClient() {
    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        AlertDialog.Builder(view.context)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok
                ) { dialog, which -> result.confirm() }
                .setNegativeButton(android.R.string.cancel
                ) { dialog, which -> result.cancel() }
                .create()
                .show()
        return true
    }
}
