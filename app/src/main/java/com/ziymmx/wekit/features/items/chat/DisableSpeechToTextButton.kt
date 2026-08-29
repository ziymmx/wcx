package com.ziymmx.wekit.features.items.chat

import android.view.ViewGroup
import android.widget.FrameLayout
import com.tencent.mm.pluginsdk.ui.chat.ChatFooter
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.utils.findViewWhich
import com.ziymmx.wekit.ui.utils.removeSelf
import com.ziymmx.wekit.utils.android.constructor

@Feature(name = "禁用输入框快捷语音转文字", categories = ["聊天"], description = "隐藏输入框右侧的语音转文字按钮")
object DisableSpeechToTextButton : SwitchFeature() {

    override fun onEnable() {
        ChatFooter::class.constructor.hookAfter {
            val chatFooter = thisObject as ChatFooter
            val button = chatFooter.findViewWhich<FrameLayout> { it.javaClass.name == "com.tencent.mm.pluginsdk.ui.SpeechInputLayout" }!!
            (((button.parent as ViewGroup).parent as ViewGroup).parent as ViewGroup).removeSelf()
        }
    }
}
