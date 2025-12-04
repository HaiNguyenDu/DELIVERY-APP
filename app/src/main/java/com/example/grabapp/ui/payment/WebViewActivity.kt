package com.example.grabapp.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    companion object { const val EXTRA_URL = "extra_url" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val wv = WebView(this)
        setContentView(wv)
        wv.settings.javaScriptEnabled = true
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.let { uri ->
                    if (uri.toString().startsWith("https://yourapp.com/payment/success")) {
                        setResult(Activity.RESULT_OK, Intent().putExtra("url", uri.toString()))
                        finish()
                        return true
                    }
                }
                return false
            }
        }
        intent?.getStringExtra(EXTRA_URL)?.let { wv.loadUrl(it) }
    }
}