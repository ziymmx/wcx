@file:Suppress("NOTHING_TO_INLINE")

package com.ziymmx.wekit.utils

import java.nio.ByteBuffer

inline fun ByteArray.toByteBuffer() = ByteBuffer.wrap(this)

