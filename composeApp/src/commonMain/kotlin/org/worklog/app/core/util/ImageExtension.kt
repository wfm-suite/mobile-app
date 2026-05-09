package org.worklog.app.core.util

expect fun ByteArray.resizeAndCompress(
    maxWidth: Int,
    maxHeight: Int,
    quality: Int = 80
): ByteArray