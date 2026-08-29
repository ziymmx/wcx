package com.ziymmx.wekit.features.api.ui

import android.app.Activity
import com.tencent.mm.ui.LauncherUI
import com.ziymmx.wekit.features.core.ApiFeature
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.ui.utils.LifecycleOwnerProvider
import com.ziymmx.wekit.ui.utils.rootView
import com.ziymmx.wekit.ui.utils.setLifecycleOwner

@Feature(name = "Compose 生命周期提供方", categories = ["API"])
object WeViewTreeLifecycleProvider : ApiFeature() {

    override fun onEnable() {
        LauncherUI::class.hookAfterOnCreate {
            val activity = thisObject as Activity

            val lifecycleOwner = LifecycleOwnerProvider.lifecycleOwner

            val decorView = activity.window.decorView
            decorView.setLifecycleOwner(lifecycleOwner)
            activity.rootView.setLifecycleOwner(lifecycleOwner)
        }
    }
}
