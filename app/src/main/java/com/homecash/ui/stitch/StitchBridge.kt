package com.homecash.ui.stitch

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.homecash.HomeCashApp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.notifications.AppNotificationCenter

/**
 * JavaScriptInterface connecting Stitch HTML/Tailwind screens directly with
 * native Android functionality, Room database, session storage, sound, and notifications.
 */
class StitchBridge(
    private val activity: Activity,
    private val webView: WebView,
    private val app: HomeCashApp,
    private val onNavigateCallback: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun navigate(screen: String) {
        mainHandler.post {
            AppSoundManager.click(webView)
            onNavigateCallback(screen)
        }
    }

    @JavascriptInterface
    fun playClick() {
        mainHandler.post {
            AppSoundManager.click(webView)
        }
    }

    @JavascriptInterface
    fun playCash() {
        mainHandler.post {
            AppSoundManager.cashRegister(webView)
        }
    }

    @JavascriptInterface
    fun postNotification(title: String, message: String, category: String) {
        mainHandler.post {
            AppNotificationCenter.post(title, message, category)
        }
    }

    @JavascriptInterface
    fun shareText(text: String) {
        mainHandler.post {
            AppSoundManager.click(webView)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            activity.startActivity(Intent.createChooser(sendIntent, "مشاركة التقرير عبر واتساب"))
        }
    }

    @JavascriptInterface
    fun setSession(userId: String) {
        mainHandler.post {
            app.repository.setActiveUser(userId)
        }
    }

    @JavascriptInterface
    fun getSession(): String {
        return app.repository.activeUserId()
    }

    @JavascriptInterface
    fun logout() {
        mainHandler.post {
            AppSoundManager.click(webView)
            com.homecash.core.simulation.MockSmsSimulationService.stopSimulation()
            app.repository.setActiveUser("")
            onNavigateCallback("login")
        }
    }

    @JavascriptInterface
    fun triggerSimulation(type: String) {
        mainHandler.post {
            when (type) {
                "vodafone" -> {
                    AppSoundManager.cashRegister(webView)
                    AppNotificationCenter.post(
                        "وارد كاش: +500.00 ج.م ⚡ فودافون كاش",
                        "تم استلام 500 ج.م من 01012345678 على خط المحل الأساسي (SIM 1). الرصيد الجديد: 15,950 ج.م",
                        "وارد"
                    )
                }
                "instapay" -> {
                    AppSoundManager.cashRegister(webView)
                    AppNotificationCenter.post(
                        "صادر كاش: -1,000.00 ج.م ⚡ إنستاباي InstaPay",
                        "تم تحويل 1,000 ج.م إلى حساب 01098765432 عبر إنستاباي. عمولة الكاشير: +10.00 ج.م",
                        "صادر"
                    )
                }
                "flag" -> {
                    AppSoundManager.click(webView)
                    AppNotificationCenter.post(
                        "تنبيه فلاج: اختلاف رصيد بمقدار 100 ج.م",
                        "رصيد رسالة اتصالات كاش يختلف عن الرصيد الدفتري المسجل.",
                        "مراجعة"
                    )
                }
            }
        }
    }
}
