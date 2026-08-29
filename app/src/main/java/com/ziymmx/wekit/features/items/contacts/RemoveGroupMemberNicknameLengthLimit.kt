package com.ziymmx.wekit.features.items.contacts

import android.view.View
import android.widget.TextView
import dev.ujhhgtg.reflekt.reflekt
import com.ziymmx.wekit.features.api.ui.WeChatMessageViewApi
import com.ziymmx.wekit.features.core.Feature

import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.utils.HookParam

@Feature(
    name = "解除群成员昵称长度限制",
    categories = ["聊天"],
    description = "移除微信为群成员昵称设置的固定最大宽度, 让昵称尽可能使用消息区域的可用空间"
)
object RemoveGroupMemberNicknameLengthLimit : SwitchFeature(),
    WeChatMessageViewApi.ICreateViewListener {

    override fun onEnable() {
        WeChatMessageViewApi.addListener(this)
    }

    override fun onDisable() {
        WeChatMessageViewApi.removeListener(this)
    }

    override fun onCreateView(param: de.robv.android.xposed.XC_MethodHook.MethodHookParam, view: View) {
        val msgInfo = WeChatMessageViewApi.getMsgInfoFromParam(param)
        if (!msgInfo.isInGroupChat) return
        if (msgInfo.isSend != 0) return

        val textView = view.tag.reflekt()
            .firstField { name = "userTV"; superclass() }
            .get() as? TextView ?: return

        // WeChat's shared userTV style caps the label at 240dp. Reset only that cap;
        // the parent layout still limits the label to the actual available screen width.
        textView.maxWidth = Int.MAX_VALUE
    }
}
