package com.ziymmx.wekit.features.items.contacts

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ziymmx.wekit.features.api.core.WeApi
import com.ziymmx.wekit.features.core.ClickableFeature
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.ui.content.AlertDialogContent
import com.ziymmx.wekit.ui.content.Button
import com.ziymmx.wekit.ui.content.TextButton
import com.ziymmx.wekit.ui.utils.showComposeDialog
import com.ziymmx.wekit.utils.android.showToast

@Feature(name = "跳转对话", categories = ["联系人与群组"], description = "打开指定微信 ID 的对话/好友主页/好友设置界面")
object OpenConversation : ClickableFeature() {

    override val noSwitchWidget = true

    override fun onClick(context: ComponentActivity) {
        showOpenConversationDialog(context)
    }
}

/** 供首页菜单等入口复用: 打开「跳转对话」对话框。 */
fun showOpenConversationDialog(context: Context) {
    showComposeDialog(context) {
            var wxId by remember { mutableStateOf("") }
            AlertDialogContent(
                title = { Text("跳转对话") },
                text = {
                    TextField(
                        value = wxId,
                        onValueChange = { wxId = it },
                        label = { Text("微信 ID") })
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (wxId.isBlank()) {
                            showToast(context, "微信 ID 为空!")
                            return@TextButton
                        }
                        WeApi.openContact(context, wxId, WeApi.OpenContactDestination.HOMEPAGE)
                    }) { Text("好友主页") }

                    TextButton(onClick = {
                        if (wxId.isBlank()) {
                            showToast(context, "微信 ID 为空!")
                            return@TextButton
                        }
                        WeApi.openContact(context, wxId, WeApi.OpenContactDestination.SETTINGS)
                    }) { Text("好友设置") }

                    Button(onClick = {
                        if (wxId.isBlank()) {
                            showToast(context, "微信 ID 为空!")
                            return@Button
                        }
                        WeApi.openContact(context, wxId, WeApi.OpenContactDestination.CONVERSATION)
                    }) { Text("对话") }
                })
    }
}
