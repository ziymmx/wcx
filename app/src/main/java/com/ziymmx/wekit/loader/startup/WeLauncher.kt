package com.ziymmx.wekit.loader.startup

import android.content.Context
import android.content.res.Resources
import com.tencent.mm.boot.BuildConfig
import com.ziymmx.wekit.constants.PackageNames
import com.ziymmx.wekit.constants.Preferences
import com.ziymmx.wekit.dexkit.cache.DexCacheManager
import com.ziymmx.wekit.features.core.FeaturesLoader
import com.ziymmx.wekit.dynamic.LocalAdaptationEngine
import com.ziymmx.wekit.dynamic.SelfHealingMonitor
import com.ziymmx.wekit.loader.utils.ActivityProxy
import com.ziymmx.wekit.loader.utils.ParcelableFixer
import com.ziymmx.wekit.utils.HostInfo
import com.ziymmx.wekit.utils.RuntimeConfig
import com.ziymmx.wekit.utils.TargetProcesses
import com.ziymmx.wekit.utils.WeLogger
import com.ziymmx.wekit.utils.hookBeforeDirectly
import com.ziymmx.wekit.utils.invokeOriginal
import com.ziymmx.wekit.utils.reflection.int

object WeLauncher {

    fun init(context: Context) {
        WeLogger.d(TAG, "loading in process name=${TargetProcesses.currentName}, type=${TargetProcesses.currentType}")

        ParcelableFixer.init()

        DexCacheManager.init(
            if (!Preferences.resetDexCacheOnHotUpdate) "${HostInfo.versionName}${HostInfo.versionCode}"
            else "${BuildConfig.VERSION_NAME}${BuildConfig.VERSION_CODE}${BuildConfig.CLIENT_VERSION_ARM64}"
        )

        if (TargetProcesses.isInMain) {
            val appContext = context.applicationContext ?: context
            ActivityProxy.init(appContext)
            LocalAdaptationEngine.init(appContext)
            SelfHealingMonitor.init()

            val prefs =
                context.getSharedPreferences("${PackageNames.WECHAT}_preferences", Context.MODE_PRIVATE)
            RuntimeConfig.mmPrefs = prefs

            // fix up Jetpack Compose
            // fuck you google
            Resources::class.java.getDeclaredMethod("getString", int).hookBeforeDirectly {
                result = runCatching { invokeOriginal() }.getOrNull() ?: "null"
            }
        }

        runCatching {
            FeaturesLoader.loadFeatures()
        }.onFailure { WeLogger.e(TAG, "failed to load hooks", it) }
    }

    private const val TAG = "WeLauncher"
}
