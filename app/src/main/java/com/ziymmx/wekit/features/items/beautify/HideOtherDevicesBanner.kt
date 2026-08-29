package com.ziymmx.wekit.features.items.beautify

import android.view.View
import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature

@Feature(
    name = "隐藏其他设备横幅", categories = ["界面美化"],
    description = "隐藏主页顶部其他设备已登录横幅"
)
object HideOtherDevicesBanner : SwitchFeature(), IResolveDex {

    private val methodSetOtherOnlineBannerVisibility by dexMethod {
        searchPackages("com.tencent.mm.ui.conversation.banner")
        matcher {
            paramTypes("int")
            returnType = "void"
            usingEqStrings(
                "com/tencent/mm/ui/conversation/banner/OtherOnlineBanner",
                "setVisibility"
            )
        }
    }

    override fun onEnable() {
        methodSetOtherOnlineBannerVisibility.hookBefore {
            if (args.isNotEmpty() && args[0] is Int) {
                args[0] = View.GONE
            }
        }
    }
}

