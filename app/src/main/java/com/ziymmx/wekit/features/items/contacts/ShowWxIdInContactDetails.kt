package com.ziymmx.wekit.features.items.contacts

import android.app.Activity
import com.ziymmx.wekit.features.api.ui.WeContactPrefsScreenApi
import com.ziymmx.wekit.features.api.ui.WeContactPrefsScreenApi.IContactInfoProvider
import com.ziymmx.wekit.features.api.ui.WeContactPrefsScreenApi.PreferenceItem
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.utils.android.copyToClipboard
import com.ziymmx.wekit.utils.android.currentWxId
import com.ziymmx.wekit.utils.android.showToast

@Feature(name = "显示微信 ID", categories = ["联系人与群组", "联系人详情页面"], description = "在联系人与群组详情页面显示微信 ID")
object ShowWxIdInContactDetails : SwitchFeature(), IContactInfoProvider {

    private const val PREF_KEY = "wxid_display"

    override fun getContactInfoItem(activity: Activity): List<PreferenceItem> {
        val wxId = activity.currentWxId

        return listOf(
            PreferenceItem(
                key = PREF_KEY,
                title = "微信 ID: ${wxId ?: "获取失败"}",
                position = 1
            )
        )
    }

    override fun onItemClick(activity: Activity, key: String): Boolean {
        if (key != PREF_KEY) return false

        val wxId = activity.currentWxId ?: return true

        copyToClipboard(activity, wxId)
        showToast(activity, "已复制")
        return true
    }

    override fun onEnable() {
        WeContactPrefsScreenApi.addProvider(this)
    }

    override fun onDisable() {
        WeContactPrefsScreenApi.removeProvider(this)
    }
}
