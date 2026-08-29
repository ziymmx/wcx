package com.ziymmx.wekit.features.items.chat

import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.utils.reflection.BInt
import com.ziymmx.wekit.utils.reflection.bool

@Feature(name = "禁用消息折叠", categories = ["聊天"], description = "阻止聊天消息被折叠")
object DisableMessageCollapsing : SwitchFeature(), IResolveDex {

    private val methodFoldMsg by dexMethod {
        matcher {
            usingStrings(".msgsource.sec_msg_node.clip-len")
            paramTypes(BInt, CharSequence::class.java, null, bool, null, null)
        }
    }

    override fun onEnable() {
        methodFoldMsg.hookBefore {
            try {
                // 仅当原方法返回 void 时才设置 result = null，避免对非 void 方法造成 ClassCastException
                if (method is java.lang.reflect.Method) {
                    val returnType = (method as java.lang.reflect.Method).returnType
                    if (returnType == Void.TYPE) {
                        result = null
                    }
                }
            } catch (e: Throwable) {
                // 兜底异常捕获，防止单条 Hook 异常导致微信主线程崩溃
            }
        }
    }
}
