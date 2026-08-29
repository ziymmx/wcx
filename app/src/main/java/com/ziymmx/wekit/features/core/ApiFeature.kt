package com.ziymmx.wekit.features.core

import com.ziymmx.wekit.utils.TargetProcesses

abstract class ApiFeature : BaseFeature() {

    override fun startup() {
        if (!TargetProcesses.isInMain) return
        enable()
    }
}
