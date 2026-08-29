package com.ziymmx.wekit.features.items.chat

import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.outlined.Download
import com.ziymmx.wekit.features.api.core.WeMessageApi
import com.ziymmx.wekit.features.api.core.models.MessageType
import com.ziymmx.wekit.features.api.ui.WeChatMessageContextMenuApi
import com.ziymmx.wekit.features.core.Feature
import com.ziymmx.wekit.features.core.SwitchFeature
import com.ziymmx.wekit.ui.utils.DownloadIcon
import com.ziymmx.wekit.utils.WeLogger
import com.ziymmx.wekit.utils.android.showToastSuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Feature(name = "图片保存到本地", categories = ["聊天"], description = "在图片消息菜单添加保存按钮, 允许将图片文件缓存并保存到本地")
object DownloadImagesToLocalStorage : SwitchFeature(), WeChatMessageContextMenuApi.IMenuItemsProvider {

    private const val TAG = "DownloadImagesToLocalStorage"

    override fun onEnable() {
        WeChatMessageContextMenuApi.addProvider(this)
    }

    override fun onDisable() {
        WeChatMessageContextMenuApi.removeProvider(this)
    }

    override fun getMenuItems(): List<WeChatMessageContextMenuApi.MenuItem> {
        return listOf(
            WeChatMessageContextMenuApi.MenuItem(
                777021,
                "下载",
                DownloadIcon,
                MaterialSymbols.Outlined.Download,
                { msgInfo -> msgInfo.type == MessageType.IMAGE }
            ) { _, _, msgInfo ->
                CoroutineScope(Dispatchers.IO).launch {
                    val path = WeMessageApi.downloadImage(msgInfo.serverId) ?: run {
                        WeLogger.e(TAG, "failed to cache & download image")
                        showToastSuspend("图片下载失败! 查看日志以了解错误详情")
                        return@launch
                    }
                    showToastSuspend("已将图片下载到 $path")
                }
            }
        )
    }
}
