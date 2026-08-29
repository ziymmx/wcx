package com.ziymmx.wekit.features.items.home_screen_menu

import com.tencent.mm.ui.LauncherUI

import com.ziymmx.wekit.features.api.ui.WeHomeScreenPopupMenuApi
import com.ziymmx.wekit.features.core.Feature

import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.features.items.contacts.showOpenConversationDialog
import com.ziymmx.wekit.ui.utils.ChatInfoIcon
import com.ziymmx.wekit.utils.HookParam

@Feature(
    name = "跳转对话菜单",
    categories = ["首页右上角菜单"],
    description = "在首页右上角菜单添加「跳转对话」选项"
)
object OpenConversationMenu : SwitchFeature(), WeHomeScreenPopupMenuApi.IMenuItemsProvider {

    override fun onEnable() {
        WeHomeScreenPopupMenuApi.addProvider(this)
    }

    override fun onDisable() {
        WeHomeScreenPopupMenuApi.removeProvider(this)
    }

    override fun getMenuItems(param: de.robv.android.xposed.XC_MethodHook.MethodHookParam): List<WeHomeScreenPopupMenuApi.MenuItem> {
        return listOf(
            WeHomeScreenPopupMenuApi.MenuItem(
                777025, ("跳转对话"), ChatInfoIcon
            ) {
                showOpenConversationDialog(LauncherUI.getInstance()!!)
            }
        )
    }
}
