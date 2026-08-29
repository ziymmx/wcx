package com.ziymmx.wekit.features.items.miniapps

import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexConstructor
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature

@Feature(name = "伪装宿主版本", categories = ["小程序"], description = "解决提示版本较低无法使用部分小程序")
object SpoofHostVersion : SwitchFeature(), IResolveDex {

    override fun onEnable() {
        ctorCgiLaunchWxaAppFunc1122.hookBefore {
            args[6] = 9999
        }
    }

    private val ctorCgiLaunchWxaAppFunc1122 by dexConstructor {
        matcher {
            usingEqStrings(
                "MicroMsg.AppBrand.CgiLaunchWxaApp|func:1122",
                "<init> cgiHash[%d], username[%s] appId[%s] sync[%b] sessionId[%s] instanceId[%s] libVersion[%d], source:%s, launchMode:%d, migrate:%b, fallback:%b"
            )
        }
    }
}
