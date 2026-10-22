package com.homecash.ui.stitch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.homecash.HomeCashApp

object StitchView {

    fun resolveScreenUrl(screenKey: String): String {
        return when (screenKey.lowercase()) {
            "login", "_8" -> "file:///android_asset/stitch/_8/code.html"
            "step1", "1_3" -> "file:///android_asset/stitch/1_3/code.html"
            "step2", "2_3" -> "file:///android_asset/stitch/2_3/code.html"
            "step3", "3_3_2" -> "file:///android_asset/stitch/3_3_2/code.html"
            "dashboard", "home", "_4" -> "file:///android_asset/stitch/_4/code.html"
            "transactions", "_15" -> "file:///android_asset/stitch/_15/code.html"
            "reports", "_2" -> "file:///android_asset/stitch/_2/code.html"
            "settings", "_17" -> "file:///android_asset/stitch/_17/code.html"
            "shift", "shift_closing", "shift_closing_1" -> "file:///android_asset/stitch/shift_closing_1/code.html"
            "notifications", "_12" -> "file:///android_asset/stitch/_12/code.html"
            "language", "language_selector_modal" -> "file:///android_asset/stitch/language_selector_modal/code.html"
            "pin" -> "file:///android_asset/stitch/pin/code.html"
            "counterparty" -> "file:///android_asset/stitch/counterparty_quick_view/code.html"
            "details", "_18" -> "file:///android_asset/stitch/_18/code.html"
            else -> "file:///android_asset/stitch/$screenKey/code.html"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun createWebView(
        activity: Activity,
        app: HomeCashApp,
        onNavigate: (String) -> Unit
    ): WebView {
        return WebView(activity).apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                databaseEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
            }

            val bridge = StitchBridge(activity, this, app) { target ->
                val targetUrl = resolveScreenUrl(target)
                this.loadUrl(targetUrl)
            }
            addJavascriptInterface(bridge, "HomeCashBridge")

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    if (url.startsWith("file:///android_asset/stitch/")) {
                        return false
                    }
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    injectStitchBridgeScript(view, url ?: "")
                }
            }
        }
    }

    private fun injectStitchBridgeScript(webView: WebView?, currentUrl: String) {
        val script = """
            (function() {
                if (window.__stitch_injected) return;
                window.__stitch_injected = true;

                // Tactile Audio & Haptics on all user touch events
                document.addEventListener('click', function(e) {
                    var el = e.target.closest('button, a, input, select');
                    if (el && window.HomeCashBridge) {
                        window.HomeCashBridge.playClick();
                    }
                }, true);

                // Universal Bottom Navigation Handler
                var navLinks = document.querySelectorAll('nav a, nav button');
                navLinks.forEach(function(link) {
                    link.addEventListener('click', function(e) {
                        var text = this.innerText || this.textContent || '';
                        if (text.indexOf('الرئيسية') !== -1) {
                            e.preventDefault();
                            e.stopPropagation();
                            window.HomeCashBridge.navigate('dashboard');
                        } else if (text.indexOf('المعاملات') !== -1) {
                            e.preventDefault();
                            e.stopPropagation();
                            window.HomeCashBridge.navigate('transactions');
                        } else if (text.indexOf('التقارير') !== -1) {
                            e.preventDefault();
                            e.stopPropagation();
                            window.HomeCashBridge.navigate('reports');
                        } else if (text.indexOf('الإعدادات') !== -1) {
                            e.preventDefault();
                            e.stopPropagation();
                            window.HomeCashBridge.navigate('settings');
                        }
                    }, true);
                });

                // Screen-Specific Seamless Integrations
                var url = window.location.href;

                // 1. Login Screen (_8)
                if (url.indexOf('_8') !== -1) {
                    var loginForm = document.getElementById('login-form');
                    var submitBtn = document.getElementById('btn-submit');
                    var langBtn = document.getElementById('btn-lang');
                    var googleBtn = document.getElementById('btn-google-login');

                    function doLogin() {
                        if (window.HomeCashBridge) {
                            window.HomeCashBridge.setSession('admin');
                            window.HomeCashBridge.playCash();
                            window.HomeCashBridge.navigate('step1');
                        }
                    }

                    if (loginForm) {
                        loginForm.onsubmit = function(e) {
                            e.preventDefault();
                            doLogin();
                            return false;
                        };
                    }
                    if (submitBtn) {
                        submitBtn.onclick = function(e) {
                            e.preventDefault();
                            doLogin();
                        };
                    }
                    if (googleBtn) {
                        googleBtn.onclick = function(e) {
                            e.preventDefault();
                            doLogin();
                        };
                    }
                    if (langBtn) {
                        langBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('language');
                        };
                    }
                }

                // 2. Step 1: Device Linking (1_3)
                if (url.indexOf('1_3') !== -1) {
                    var nextBtn = document.querySelector('aside button:last-child');
                    var skipBtn = document.querySelector('aside button:first-child');
                    var backBtn = document.querySelector('header button');

                    if (nextBtn) {
                        nextBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('step2');
                        };
                    }
                    if (skipBtn) {
                        skipBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('step2');
                        };
                    }
                    if (backBtn) {
                        backBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('login');
                        };
                    }
                }

                // 3. Step 2: Account Type (2_3)
                if (url.indexOf('2_3') !== -1) {
                    window.handleContinue = function() {
                        window.HomeCashBridge.navigate('step3');
                    };
                    var contBtns = document.querySelectorAll('button');
                    contBtns.forEach(function(b) {
                        if ((b.innerText || '').indexOf('المتابعة') !== -1) {
                            b.onclick = function(e) {
                                e.preventDefault();
                                window.HomeCashBridge.navigate('step3');
                            };
                        }
                    });
                }

                // 4. Step 3: Wallets Config (3_3_2)
                if (url.indexOf('3_3_2') !== -1) {
                    var allBtns = document.querySelectorAll('button');
                    allBtns.forEach(function(b) {
                        var txt = b.innerText || b.textContent || '';
                        if (txt.indexOf('إتمام') !== -1 || txt.indexOf('بدء الاستخدام') !== -1) {
                            b.onclick = function(e) {
                                e.preventDefault();
                                window.HomeCashBridge.playCash();
                                window.HomeCashBridge.navigate('transactions');
                            };
                        }
                    });
                }

                // 5. Settings Screen (_17)
                if (url.indexOf('_17') !== -1) {
                    var closeShiftLink = document.querySelector('a[href="#close-shift"]');
                    var reviewSmsLink = document.querySelector('a[href="#review-sms"]');
                    var balanceMatchLink = document.querySelector('a[href="#balance-match"]');
                    var themeLangLink = document.querySelector('a[href="#theme-language"]');
                    var exportBtn = document.getElementById('export-reports-btn');
                    var endDemoBtn = document.getElementById('end-demo-btn');

                    if (closeShiftLink) {
                        closeShiftLink.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('shift');
                        };
                    }
                    if (reviewSmsLink) {
                        reviewSmsLink.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('notifications');
                        };
                    }
                    if (balanceMatchLink) {
                        balanceMatchLink.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('dashboard');
                        };
                    }
                    if (themeLangLink) {
                        themeLangLink.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.navigate('language');
                        };
                    }
                    if (exportBtn) {
                        exportBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.shareText('كشف حساب كامل — دفتر المحافظ HomeCash\nمحل الباشا فون • إجمالي الحركات 41 معاملة\nصافي السيولة: 27,700 ج.م');
                        };
                    }
                }

                // 6. Reports Screen (_2)
                if (url.indexOf('_2') !== -1) {
                    var repExportBtns = document.querySelectorAll('button');
                    repExportBtns.forEach(function(b) {
                        var txt = b.innerText || '';
                        if (txt.indexOf('تصدير') !== -1 || txt.indexOf('Excel') !== -1 || txt.indexOf('PDF') !== -1) {
                            b.onclick = function(e) {
                                e.preventDefault();
                                window.HomeCashBridge.shareText('تقرير اليومية والسيولة النقدية — محل الباشا فون\nالوارد: 27,700 ج.م\nالصادر: 4,300 ج.م\nالعمولات: 3,480 ج.م');
                            };
                        }
                    });
                }

                // 7. Shift Closing (shift_closing_1)
                if (url.indexOf('shift_closing') !== -1) {
                    var shareShiftBtn = document.getElementById('btn-share-shift-report');
                    var confirmShiftBtn = document.getElementById('btn-confirm-shift-close');

                    if (shareShiftBtn) {
                        shareShiftBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.shareText('تقرير إقفال الوردية — كاشير أحمد محمود\nالنقدية الفعلية بالدرج: 10,635.00 ج.م\nالدرج مطابق بنسبة 100%');
                        };
                    }
                    if (confirmShiftBtn) {
                        confirmShiftBtn.onclick = function(e) {
                            e.preventDefault();
                            window.HomeCashBridge.playCash();
                            window.HomeCashBridge.postNotification('تم تقفيل الوردية والدرج بنجاح', 'تم ترحيل 10,635.00 ج.م إلى الحساب اليومي النهائي.', 'الوردية');
                            alert('تم اعتماد إقفال الوردية وترحيل الحساب بنجاح!');
                            window.HomeCashBridge.navigate('transactions');
                        };
                    }
                }

                // 8. Notifications (_12)
                if (url.indexOf('_12') !== -1) {
                    var resolveBtns = document.querySelectorAll('button');
                    resolveBtns.forEach(function(b) {
                        if ((b.innerText || '').indexOf('حل الفلاج') !== -1) {
                            b.onclick = function(e) {
                                e.preventDefault();
                                window.HomeCashBridge.navigate('dashboard');
                            };
                        }
                    });
                }
            })();
        """.trimIndent()
        webView?.evaluateJavascript(script, null)
    }
}
