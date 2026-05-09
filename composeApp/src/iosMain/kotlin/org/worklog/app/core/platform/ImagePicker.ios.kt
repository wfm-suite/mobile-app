package org.worklog.app.core.platform

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy
import kotlin.getValue

actual class ImagePicker actual constructor() {
    private val rootController: UIViewController =
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: error("RootViewController not found")

    private var onImagePicked: (ByteArray) -> Unit = {}

    private val imagePickerController: UIImagePickerController by lazy {
        UIImagePickerController().apply {
            sourceType =
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            allowsEditing = false
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private val delegate = object : NSObject(),
        UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingImage: UIImage,
            editingInfo: Map<Any?, *>?
        ) {
            val data = UIImageJPEGRepresentation(didFinishPickingImage, 1.0)
            val bytes = data?.toByteArray()
            if (bytes != null) {
                onImagePicked(bytes)
            }
            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }

    @Composable
    actual fun RegisterPicker(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }

    actual fun pickImage() {
        imagePickerController.delegate = delegate
        rootController.presentViewController(imagePickerController, true, null)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    val bytes = ByteArray(length.toInt())
    memcpy(bytes.refTo(0), this.bytes, length)
    return bytes
}