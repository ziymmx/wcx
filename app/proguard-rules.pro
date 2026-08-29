# ─── Xposed Module Entry Points ────────────────────────────────────
# These MUST be kept with original names — Xposed framework loads them
-keep class de.robv.android.xposed.** { *; }
-keep class io.github.libxposed.** { *; }
-keep class com.ziymmx.wekit.entry.** { *; }
-keep class com.ziymmx.wekit.application.** { *; }

# ─── Feature / Hook Classes ─────────────────────────────────────────
# 原先这里是一条 -keep class com.ziymmx.wekit.features.** { *; }
# 它会无条件保留 features 包下全部 343 个功能, 使 R8 无法裁剪,
# 功能白名单 (features.whitelist) 因此完全失效。
#
# 改为只保留基类与白名单内的功能。白名单外的功能虽然仍会被编译,
# 但不会进入 FeaturesProvider.ALL_HOOK_ITEMS, 成为不可达代码后被 R8 移除。
#
# 注意: 下面的列表必须与仓库根目录的 features.whitelist 保持一致,
# 否则新增功能会被混淆或裁掉。
-keep class com.ziymmx.wekit.features.core.** { *; }

-keep class com.ziymmx.wekit.features.items.chat.AntiMessageRecall { *; }
-keep class com.ziymmx.wekit.features.items.system.PreventXposedDetection { *; }
-keep class com.ziymmx.wekit.features.items.system.SpoofEnvironment { *; }
-keep class com.ziymmx.wekit.features.items.system.HideModuleFromAppList { *; }
-keep class com.ziymmx.wekit.features.items.system.DisableHostHotUpdates { *; }
-keep class com.ziymmx.wekit.features.items.system.ForceTabletMode { *; }
-keep class com.ziymmx.wekit.features.items.debug.ResetDexCache { *; }

-keep class com.ziymmx.wekit.features.api.core.WeXmlParserApi { *; }
-keep class com.ziymmx.wekit.features.api.core.WeDatabaseApi { *; }
-keep class com.ziymmx.wekit.features.api.core.WeMessageApi { *; }
-keep class com.ziymmx.wekit.features.api.net.WeNetSceneApi { *; }

-keep,allowobfuscation class com.ziymmx.wekit.hooks.** { *; }
-keep,allowobfuscation class com.ziymmx.wekit.datas.** { *; }

# Keep annotation-annotated members (used by compile-time processors)
-keepclassmembers,allowobfuscation class * {
    @com.ziymmx.wekit.annotations.* *;
}

# ─── Kotlin ────────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-keep class kotlin.coroutines.Continuation { *; }
-dontwarn kotlinx.coroutines.**

# kotlin-reflect 经传递依赖存在于 APK：R8 不得裁剪其内建表
# （否则 KotlinBuiltIns.getBuiltInClassByFqName 返回 null → 启用功能时 IllegalStateException）
-keep class kotlin.reflect.** { *; }
-dontwarn kotlin.reflect.**

# ─── Serialization ──────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-keep class kotlinx.serialization.** { *; }
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.ziymmx.wekit.**$$serializer { *; }
-keepclassmembers class com.ziymmx.wekit.** {
    *** Companion;
}
-keepclasseswithmembers class com.ziymmx.wekit.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ─── Room ───────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ─── Compose ────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ─── Third-party (dontwarn only, allow R8 optimization) ──────────────
-dontwarn com.alibaba.fastjson2.**
-dontwarn io.netty.**
-dontwarn com.google.protobuf.**
-dontwarn com.tencent.wcdb.**
-dontwarn org.slf4j.**
-dontwarn org.mozilla.javascript.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn io.ktor.**
-dontwarn com.materialkolor.**
-dontwarn miuix.**
-dontwarn javax.**
-dontwarn java.lang.invoke.**

# ─── WeChat Stubs ───────────────────────────────────────────────────
-keep class com.tencent.mm.** { *; }

# ─── Obfuscation Enhancements ───────────────────────────────────────
-repackageclasses
-allowaccessmodification
-overloadaggressively
-useuniqueclassmembernames

# ─── Attributes ─────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable

# ─── Keep resource names used in code ───────────────────────────────
-keepclassmembers class **.R$* {
    public static <fields>;
}

# R$plurals 类本身必须保留：R8 会因 R$plurals 成员被常量内联而整体移除该类，
# 但 Kotlin 对 pluralStringResource(R.plurals.*) 的编译引用仍指向它。
-keep class **.R$plurals { *; }