package com.ziymmx.wekit.features.api.ui

import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.ApiFeature
import com.ziymmx.wekit.features.core.Feature

@Feature(name = "微信主屏幕美化服务", categories = ["API"], description = "提供美化微信主屏幕的能力")
object WeMainActivityBeautifyApi : ApiFeature(), IResolveDex {

    val methodDoOnCreate by dexMethod {
        matcher {
            declaredClass = "com.tencent.mm.ui.MainTabUI"
            usingEqStrings("MicroMsg.LauncherUI.MainTabUI", "doOnCreate")
        }
    }
}
