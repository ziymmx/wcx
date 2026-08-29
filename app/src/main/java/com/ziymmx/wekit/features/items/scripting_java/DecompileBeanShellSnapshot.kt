package com.ziymmx.wekit.features.items.scripting_java

import androidx.activity.ComponentActivity
import com.ziymmx.wekit.activity.TransparentActivity
import com.ziymmx.wekit.features.core.ClickableFeature
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.utils.registerBshSnapshotDecompileLaunchers

@Feature(name = "反编译 BeanShell 快照", categories = ["脚本 (Java)"], description = "不知道这是干啥的就别管了")
object DecompileBeanShellSnapshot : ClickableFeature() {

    override val noSwitchWidget = true

    override fun onClick(context: ComponentActivity) {
        TransparentActivity.launch(context) {
            val selectFileLauncher = registerBshSnapshotDecompileLaunchers { finish() }
            selectFileLauncher.launch("*/*")
        }
    }
}
