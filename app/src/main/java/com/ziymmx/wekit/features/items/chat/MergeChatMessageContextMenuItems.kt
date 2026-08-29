package com.ziymmx.wekit.features.items.chat

import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature

@Feature(
    name = "消息长按菜单项合并展示",
    categories = ["聊天"],
    description = "开启后, 消息长按菜单只显示一个 WCX 项, 点击后在对话框中集中展示所有模块菜单项"
)
object MergeChatMessageContextMenuItems : SwitchFeature() // actual implementation in WeChatMessageContextMenuApi
