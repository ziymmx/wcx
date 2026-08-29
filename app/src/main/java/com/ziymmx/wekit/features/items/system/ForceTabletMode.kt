package com.ziymmx.wekit.features.items.system

import android.content.Context
import androidx.compose.material3.Text
import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.content.AlertDialogContent
import com.ziymmx.wekit.ui.content.Button
import com.ziymmx.wekit.ui.content.TextButton
import com.ziymmx.wekit.ui.utils.showComposeDialog
import com.ziymmx.wekit.utils.WeLogger
import kotlin.coroutines.Continuation
import java.lang.reflect.Modifier

@Feature(name = "强制平板模式", categories = ["系统与隐私"], description = "让微信将当前设备识别为平板")
object ForceTabletMode : SwitchFeature(), IResolveDex {

    private const val TAG = "ForceTabletMode"

    /**
     * 服务端登录校验: `/cgi-bin/micromsg-bin/checkloginaspad`。
     *
     * 这是"以平板身份登录"的实质性判定 —— 微信把设备类型上报给服务器, 由服务器
     * 决定本次登录算不算平板端。只改本地 UI 而不动这里, 就会出现"本地显示平板、
     * 服务端仍记为手机"的状态不一致, 这本身就是触发风控的常见原因。
     *
     * 注意: 这是 suspend 函数, 编译后签名形如
     * `Object checkLoginAsPad(String, String, Continuation)`, 返回类型是 Object
     * 而不是 boolean。所以这里直接 `result = true`, 不做返回类型判断 —— 返回值会
     * 被 Kotlin 协程状态机当作函数结果恢复。加了 boolean 判断反而会静默失效。
     */
    private val methodCheckLoginAsPad by dexMethod(allowFailure = true) {
        searchPackages("com.tencent.mm")
        matcher {
            modifiers = Modifier.PUBLIC or Modifier.FINAL
            paramCount = 3
            paramTypes(String::class.java, String::class.java, Continuation::class.java)
            usingStrings(
                "MicroMsg.CgiCheckLoginAsPad",
                "/cgi-bin/micromsg-bin/checkloginaspad"
            )
        }
    }

    /**
     * 折叠屏判定。
     *
     * 匹配条件收紧为 包名 + 修饰符 + 参数个数 + 返回类型 + 特征字符串 五重约束,
     * 而不是只靠一两个字符串, 以降低微信改版后误匹配到别的方法的概率。
     */
    private val methodIsFoldableDevice by dexMethod(allowFailure = true) {
        searchPackages("com.tencent.mm.ui")
        matcher {
            modifiers = Modifier.PUBLIC or Modifier.STATIC
            paramCount = 0
            returnType(java.lang.Boolean.TYPE)
            usingStrings("royole", "tecno", "ro.os_foldable_screen_support")
        }
    }

    private val methodIsTablet by dexMethod(allowFailure = true) {
        matcher {
            usingEqStrings("Lenovo TB-9707F", "eebbk")
        }
    }

    override fun onEnable() {
        if (methodIsTablet.isPlaceholder) {
            WeLogger.w(TAG, "isTablet not resolved, skipping")
        } else {
            methodIsTablet.hookBefore { result = true }
        }

        if (methodIsFoldableDevice.isPlaceholder) {
            WeLogger.w(TAG, "isFoldableDevice not resolved, skipping")
        } else {
            methodIsFoldableDevice.hookBefore { result = true }
        }

        if (methodCheckLoginAsPad.isPlaceholder) {
            WeLogger.w(TAG, "CgiCheckLoginAsPad not resolved, server-side check left untouched")
        } else {
            methodCheckLoginAsPad.hookBefore { result = true }
        }
    }

    override fun onBeforeToggle(newState: Boolean, context: Context): Boolean {
        if (newState) {
            showComposeDialog(context) {
                AlertDialogContent(
                    title = { Text(text = "警告") },
                    text = { Text(text = "此功能可能导致账号异常, 确定要启用吗?") },
                    confirmButton = {
                        Button(onClick = {
                            applyToggle(true)
                            onDismiss()
                        }) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onDismiss) {
                            Text("取消")
                        }
                    }
                )
            }
            return false
        }

        return true
    }
}
