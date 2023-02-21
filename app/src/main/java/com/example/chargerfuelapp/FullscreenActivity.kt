package com.example.chargerfuelapp

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.chargerfuelapp.databinding.ActivityFullscreenBinding

class FullscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var webView: WebView
    private lateinit var splashView: View

    private val hideHandler = Handler(Looper.myLooper()!!)
    private val hideRunnable = Runnable { hideSplash() }
    private var splashShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find views in the layout
        webView = binding.webview
        splashView = binding.splashScreen

        // Hide the WebView until the splash screen is hidden
        webView.visibility = View.INVISIBLE

        // Show the splash screen
        showSplash()

        // Enable JavaScript in the WebView
        webView.settings.javaScriptEnabled = true

        // Load website in the WebView and delete the cache and history
        webView.clearCache(true)
        webView.clearHistory()
        webView.loadUrl("https://charger.food.is")

        // Set a WebViewClient to handle page navigation
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Once the page is loaded, hide the splash screen and show the WebView
                hideHandler.postDelayed(hideRunnable,0)
                if (!splashShown) {
                    hideSplash()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSplash() {
        splashView.visibility = View.VISIBLE
        splashShown = true
    }

    private fun hideSplash() {
        splashView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        splashShown = false
        hideHandler.removeCallbacks(hideRunnable)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }
}