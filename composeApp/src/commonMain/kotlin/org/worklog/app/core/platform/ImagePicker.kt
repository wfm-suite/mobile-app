package org.worklog.app.core.platform

import androidx.compose.runtime.Composable

expect class ImagePicker constructor() {

    @Composable
    fun RegisterPicker(onImagePicked: (ByteArray) -> Unit)
    fun pickImage()
}