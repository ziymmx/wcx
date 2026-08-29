package com.ziymmx.wekit.features.items.home_screen_menu

import de.robv.android.xposed.XC_MethodHook
import com.ziymmx.wekit.features.api.ui.WeHomeScreenPopupMenuApi
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.utils.CancelIcon
import com.ziymmx.wekit.utils.killHost

@Feature(name = "强行停止", categories = ["首页右上角菜单"], description = "在首页右上角菜单添加「强行停止」选项")
object KillHostProcess : SwitchFeature(), WeHomeScreenPopupMenuApi.IMenuItemsProvider {

    override fun onEnable() {
        WeHomeScreenPopupMenuApi.addProvider(this)
    }

    override fun onDisable() {
        WeHomeScreenPopupMenuApi.removeProvider(this)
    }

    override fun getMenuItems(param: XC_MethodHook.MethodHookParam): List<WeHomeScreenPopupMenuApi.MenuItem> {
        return listOf(
            WeHomeScreenPopupMenuApi.MenuItem(
                777015, "强行停止", CancelIcon
            ) {
                killHost()
            }
        )
    }
}
