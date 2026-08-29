package com.ziymmx.wekit.utils.strings

val String.isGroupChatWxId
    get() =
        this.endsWith("@chatroom") || this.endsWith("@im.chatroom")
