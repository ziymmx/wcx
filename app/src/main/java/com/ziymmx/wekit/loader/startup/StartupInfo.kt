package com.ziymmx.wekit.loader.startup

import com.ziymmx.wekit.loader.abc.IHookBridge
import com.ziymmx.wekit.loader.abc.ILoaderService

object StartupInfo {

    lateinit var loaderService: ILoaderService
    var hookBridge: IHookBridge? = null
}
