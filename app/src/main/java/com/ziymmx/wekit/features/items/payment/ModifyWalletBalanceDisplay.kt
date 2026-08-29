package com.ziymmx.wekit.features.items.payment

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ziymmx.wekit.dexkit.abc.IResolveDex
import com.ziymmx.wekit.dexkit.dsl.dexMethod
import com.ziymmx.wekit.features.core.ClickableFeature
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.preferences.WePrefs.Companion.prefOption
import com.ziymmx.wekit.ui.content.AlertDialogContent
import com.ziymmx.wekit.ui.content.Button
import com.ziymmx.wekit.ui.content.TextButton
import com.ziymmx.wekit.ui.utils.showComposeDialog
import com.ziymmx.wekit.utils.nul
import com.ziymmx.wekit.utils.reflection.BString
import com.ziymmx.wekit.utils.reflection.bool

@Feature(name = "修改显示余额", categories = ["红包与支付"], description = "伪装钱包余额文字")
object ModifyWalletBalanceDisplay : ClickableFeature(), IResolveDex {

    private const val KEY_BALANCE = "fake_wallet_balance"

    private val methodWcPayMoneyLoadingViewSetMoneyCore by dexMethod {
        matcher {
            declaredClass = "com.tencent.mm.plugin.wallet_core.ui.view.WcPayMoneyLoadingView"
            paramTypes(BString, bool, bool, bool)
            addInvoke {
                declaredClass = "com.tencent.mm.plugin.wallet_core.ui.view.WcPayMoneyLoadingView"
                name = "setFirstMoney"
            }
        }
    }

    private var balance by prefOption(KEY_BALANCE, nul<String>())

    override fun onEnable() {
        methodWcPayMoneyLoadingViewSetMoneyCore.hookBefore {
            val balance = balance ?: return@hookBefore
            args[0] = balance
        }
    }

    override fun onClick(context: ComponentActivity) {
        showComposeDialog(context) {
            var balanceInput by remember { mutableStateOf(balance ?: "") }

            AlertDialogContent(
                title = { Text("修改显示余额") },
                text = {
                    TextField(
                        value = balanceInput,
                        onValueChange = { balanceInput = it },
                        label = { Text("零钱余额 (留空不修改)") })
                },
                confirmButton = {
                    Button(onClick = {
                        balance = if (!balanceInput.isBlank())
                            balanceInput
                        else
                            null
                        onDismiss()
                    }) { Text("确定") }
                },
                dismissButton = { TextButton(onDismiss) { Text("取消") } }
            )
        }
    }
}
