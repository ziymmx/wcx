package com.ziymmx.wekit.features.items.home_screen_menu

import com.tencent.mm.ui.LauncherUI
import de.robv.android.xposed.XC_MethodHook
import com.ziymmx.wekit.BuildConfig
import com.ziymmx.wekit.features.api.ui.WeHomeScreenPopupMenuApi
import com.ziymmx.wekit.features.api.ui.WeSettingsInjector
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.utils.ExtensionIcon

@Feature(name = "模块设置", categories = ["首页右上角菜单"], description = "在首页右上角菜单添加「WCX」选项")
object ModuleSettings : SwitchFeature(), WeHomeScreenPopupMenuApi.IMenuItemsProvider {

    override fun onEnable() {
        WeHomeScreenPopupMenuApi.addProvider(this)
    }

    override fun onDisable() {
        WeHomeScreenPopupMenuApi.removeProvider(this)
    }

    override fun getMenuItems(param: XC_MethodHook.MethodHookParam): List<WeHomeScreenPopupMenuApi.MenuItem> =
        listOf(
            WeHomeScreenPopupMenuApi.MenuItem(
                0, BuildConfig.TAG, ExtensionIcon
            ) { WeSettingsInjector.openSettingsDialog(LauncherUI.getInstance()!!) }
        )
}
