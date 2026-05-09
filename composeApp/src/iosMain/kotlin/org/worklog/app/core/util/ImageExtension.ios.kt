package org.worklog.app.core.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy
import kotlin.math.min

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.resizeAndCompress(
    maxWidth: Int,
    maxHeight: Int,
    quality: Int
): ByteArray {
    // Convert ByteArray → NSData
    val nsData = this.usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }

    // Decode UIImage
    val originalImage = UIImage.imageWithData(nsData) ?: return this

    // Original dimensions
    val (originalWidth, originalHeight) = originalImage.size.useContents {
        width to height
    }

    // Scale ratio (fit into maxWidth × maxHeight, maintain aspect ratio)
    val widthRatio = maxWidth.toDouble() / originalWidth
    val heightRatio = maxHeight.toDouble() / originalHeight
    val scale = min(widthRatio, heightRatio)

    val newWidth = (originalWidth * scale)
    val newHeight = (originalHeight * scale)

    // Draw into resized context
    UIGraphicsBeginImageContextWithOptions(
        size = CGSizeMake(newWidth, newHeight),
        opaque = false,
        scale = 1.0 // fixed scale
    )

    originalImage.drawInRect(
        CGRectMake(0.0, 0.0, newWidth, newHeight)
    )

    val resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    // Compress JPEG
    val compressedData = resizedImage?.let {
        UIImageJPEGRepresentation(it, quality.toDouble() / 100.0)
    } ?: return this

    // Convert NSData → ByteArray
    val length = compressedData.length.toInt()
    val result = ByteArray(length)

    result.usePinned { pinned ->
        memcpy(
            __dst = pinned.addressOf(0),
            __src = compressedData.bytes,
            __n = length.toULong()
        )
    }

    return result
}