package com.ziymmx.wekit.features.items.miniapps

import android.webkit.ValueCallback
import android.webkit.WebView
import dev.ujhhgtg.reflekt.reflekt
import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.eruda.ErudaProvider
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.utils.TargetProcesses
import com.ziymmx.wekit.utils.WeLogger
import com.ziymmx.wekit.utils.reflection.BString
import org.luckypray.dexkit.query.enums.StringMatchType

@Feature(
    name = "Eruda 调试面板",
    categories = ["小程序"],
    description = "小程序页面注入 Eruda 调试控制台"
)
object ErudaConsole : SwitchFeature(), IResolveDex {

    private val xwebOnPageFinished by dexMethod {
        searchPackages("com.tencent.mm.plugin.appbrand.page")
        matcher {
            declaredClass {
                usingEqStrings(
                    "MicroMsg.AppBrandWebView",
                    "onReceivedHttpError, WebResourceRequest url = %s, ErrWebResourceResponse mimeType = %s, status = %d"
                )
                superClass {
                    className("com.tencent.xweb", StringMatchType.StartsWith)
                }
            }
            paramTypes("com.tencent.xweb.WebView", "java.lang.String", "android.graphics.Bitmap")
            returnType = "void"
        }
    }
    private val androidOnPageFinished by dexMethod {
        searchPackages("com.tencent.mm.plugin.appbrand.page")
        matcher {
            declaredClass {
                superClass = "android.webkit.WebViewClient"
            }
            paramCount = 2
            returnType = "void"
        }
    }

    override val shouldLoadInCurrentProcess get() = TargetProcesses.isInMain || TargetProcesses.currentType == TargetProcesses.PROC_APPBRAND

    override fun onEnable() {
        xwebOnPageFinished.hookAfter {
            WeLogger.i(TAG, "injecting into xwebOnPageFinished: ${args[0]}")
            injectEruda(args[0])
        }
        androidOnPageFinished.hookAfter {
            WeLogger.i(TAG, "injecting into androidOnPageFinished: ${args[0]}")
            injectEruda(args[0])
        }
    }

    private fun injectEruda(webView: Any) {
        try {
            when (webView) {
                is WebView -> {
                    webView.evaluateJavascript(ErudaProvider.ERUDA_JS, null)
                    webView.evaluateJavascript("eruda.init();", null)
                }

                is com.tencent.xweb.WebView -> {
                    webView.evaluateJavascript(ErudaProvider.ERUDA_JS, null)
                    webView.evaluateJavascript("eruda.init();", null)
                }

                else -> {
                    webView.reflekt().firstMethod {
                        name = "evaluateJavascript"
                        parameters(BString, ValueCallback::class)
                        superclass()
                    }.apply {
                        invoke(ErudaProvider.ERUDA_JS, null)
                        invoke("eruda.init();", null)
                    }
                }
            }
            WeLogger.i(TAG, "injected eruda")
        } catch (e: Throwable) {
            WeLogger.w(TAG, "failed to inject eruda", e)
        }
    }

    private const val TAG = "ErudaConsole"
}
