package com.ziymmx.wekit.features.items.system

import android.content.Context
import android.widget.Button
import androidx.compose.material3.Text
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dev.ujhhgtg.reflekt.reflekt
import dev.ujhhgtg.reflekt.utils.toClass
import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.content.AlertDialogContent
import com.ziymmx.wekit.ui.content.Button
import com.ziymmx.wekit.ui.content.TextButton
import com.ziymmx.wekit.ui.utils.showComposeDialog

/**
 * 强制平板模式。
 *
 * 匹配器刻意保持宽松 —— 只用特征字符串, 不加包名 / 参数个数 / 修饰符 / 返回类型
 * 约束。曾经给 isFoldableDevice 和 CgiCheckLoginAsPad 加过签名级约束, 结果在
 * 8.0.72 上匹配不到。这里改用 WeKite 那套已验证支持 8.0.65~8.0.76 的宽松匹配。
 *
 * 四个 hook 各自的作用:
 *  - methodIsTablet                         本地机型判定 (Lenovo TB-9707F / eebbk 白名单)
 *  - methodIsTablet2                        折叠屏判定
 *  - methodCgiCheckLoginAsPad               服务端登录校验, 决定本次登录算不算平板端
 *  - methodOtherDeviceLoginButtonIsVisible +
 *    LoginHistoryUI.initView                让"作为其他设备登录"的入口露出来
 */
@Feature(name = "强制平板模式", categories = ["系统与隐私"], description = "让微信将当前设备识别为平板")
object ForceTabletMode : SwitchFeature(), IResolveDex {

    private val methodIsTablet by dexMethod {
        matcher {
            usingEqStrings("Lenovo TB-9707F", "eebbk")
        }
    }

    private val methodIsTablet2 by dexMethod {
        matcher {
            usingEqStrings("MicroMsg.UIUtils", "isRoyoleFoldableDevice!!!")
        }
    }

    private val methodOtherDeviceLoginButtonIsVisible by dexMethod {
        matcher {
            usingEqStrings("loginAsOtherDeviceBtn")
        }
    }

    /**
     * 服务端登录校验 `/cgi-bin/micromsg-bin/checkloginaspad`。
     *
     * 只改本地判断而漏掉这里, 会出现"本地显示平板、服务端仍记为手机"的状态不一致。
     *
     * 注意: 这是 suspend 函数, 编译后返回类型是 Object 而非 boolean, 所以直接
     * result = true, 不做返回类型判断 —— 返回值会由协程状态机作为函数结果恢复。
     */
    private val methodCgiCheckLoginAsPad by dexMethod {
        matcher {
            usingEqStrings("MicroMsg.CgiCheckLoginAsPad", "/cgi-bin/micromsg-bin/checkloginaspad")
        }
    }

    override fun onEnable() {
        methodIsTablet.hookBefore {
            result = true
        }

        methodIsTablet2.hookBefore {
            result = true
        }

        methodOtherDeviceLoginButtonIsVisible.hookBefore {
            val view = args[0] as? Button? ?: return@hookBefore
            if (view.isGone) view.isVisible = true
        }

        "com.tencent.mm.plugin.account.ui.LoginHistoryUI".toClass().reflekt().firstMethod("initView").hookAfter {
            val btn = thisObject.reflekt().firstField {
                type = Button::class
            }.get()!! as Button
            btn.isVisible = true
        }

        methodCgiCheckLoginAsPad.hookBefore {
            result = true
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
