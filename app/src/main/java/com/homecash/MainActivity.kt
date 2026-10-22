package com.homecash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.homecash.ui.stitch.StitchView

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as HomeCashApp
        val incomingIntent = intent
        setIntent(Intent(this, MainActivity::class.java))
        app.handleOAuthIntent(incomingIntent)

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Determine starting screen based on session state
        val hasSession = app.repository.activeUserId().isNotBlank()
        val initialUrl = if (hasSession) {
            StitchView.resolveScreenUrl("transactions")
        } else {
            StitchView.resolveScreenUrl("login")
        }

        webView = StitchView.createWebView(this, app) { route ->
            val targetUrl = StitchView.resolveScreenUrl(route)
            webView?.loadUrl(targetUrl)
        }

        setContentView(webView)
        webView?.loadUrl(initialUrl)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(Intent(this, MainActivity::class.java))
        (application as HomeCashApp).handleOAuthIntent(intent)
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
