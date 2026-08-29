package com.ziymmx.wekit.utils.fs

import android.os.Environment
import com.ziymmx.wekit.BuildConfig
import com.ziymmx.wekit.constants.PackageNames
import com.ziymmx.wekit.utils.HostInfo
import java.nio.file.Path
import kotlin.io.path.div

object KnownPaths {

    val internalStorage: Path by lazy {
        Environment.getExternalStorageDirectory().asPath
    }

    val moduleData by lazy {
        (internalStorage / "Android" / "data" /
                runCatching { HostInfo.packageName }.getOrDefault(PackageNames.WECHAT) /
                BuildConfig.TAG).createDirsSafe()
    }

    val codeCacheDir: Path by lazy {
        HostInfo.application.codeCacheDir.asPath
    }

    val moduleCache by lazy {
        (internalStorage / "Android" / "data" /
                runCatching { HostInfo.packageName }.getOrDefault(PackageNames.WECHAT)
                / "cache" / BuildConfig.TAG).createDirsSafe()
    }

    val moduleAssets by lazy {
        (moduleData / "assets").createDirsSafe()
    }

    val downloads by lazy {
        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toPath() / BuildConfig.TAG)
            .createDirsSafe()
    }
}
