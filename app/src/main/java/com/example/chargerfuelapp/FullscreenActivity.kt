package com.example.chargerfuelapp

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.chargerfuelapp.databinding.ActivityFullscreenBinding
import com.example.chargerfuelapp.databinding.ErrorLayoutBinding

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
                hideHandler.postDelayed(hideRunnable, 500)
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

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                    // Show an error screen with a refresh button
                    showErrorMessage()
                } else {
                    super.onReceivedError(view, errorCode, description, failingUrl)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            splashView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE
            )
        }
        splashView.visibility = View.VISIBLE
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 500
        splashView.startAnimation(anim)
        splashShown = true

    }
    private fun hideSplash() {
        webView.visibility = View.VISIBLE

        // Add a delay of 100ms before starting the fade animation
        Handler().postDelayed({
            val anim = AlphaAnimation(1.0f, 0.0f)
            anim.duration = 500
            splashView.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    splashView.visibility = View.GONE
                    splashShown = false
                }
            })
        }, 100)

        Handler().postDelayed({
            binding.root.removeView(splashView)
            splashShown = false
        }, 650)
    }

    private fun showErrorMessage() {
        // Set the WebView to be invisible
        webView.visibility = View.INVISIBLE

        // Inflate the error layout
        val errorLayout = layoutInflater.inflate(R.layout.error_layout, null)

        // Set the refresh button click listener
        val refreshButton = errorLayout.findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener {
            errorLayout.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.reload()
        }

        // Add the error layout to the root view
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        val rootView = findViewById<View>(android.R.id.content) as FrameLayout
        rootView.addView(errorLayout, params)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }
}