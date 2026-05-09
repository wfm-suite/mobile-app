package org.worklog.app.core.platform

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual class ImagePicker actual constructor() {
    private var getContent: ActivityResultLauncher<String>? = null
    private var activity: ComponentActivity? = null

    @Composable
    actual fun RegisterPicker(onImagePicked: (ByteArray) -> Unit) {
        val context = LocalContext.current
        activity = context as ComponentActivity
        val scope = rememberCoroutineScope()

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    try {
                        val bytes = processImageOptimized(context, it)
                        withContext(Dispatchers.Main) {
                            onImagePicked(bytes)
                        }
                    } catch (e: Exception) {
                        Log.e("RegisterPicker", "Error processing image", e)
                    }
                }
            }
        }
        getContent = launcher
    }

    actual fun pickImage() {
        checkNotNull(getContent) { "RegisterPicker must be called before pickImage()" }
            .launch("image/*")
    }

    private fun processImageOptimized(context: Context, uri: Uri): ByteArray {
        // Get image dimensions without loading the full bitmap
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }

        // Calculate sample size to reduce memory usage
        val maxDimension = 1920 // Max width or height
        options.inSampleSize = calculateInSampleSize(options, maxDimension, maxDimension)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory

        // Decode with sample size
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: throw Exception("Failed to decode image")

        // Get EXIF orientation
        val exif = context.contentResolver.openInputStream(uri)?.use { stream ->
            ExifInterface(stream)
        }

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        // Rotate if needed
        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
            else -> bitmap
        }

        // Compress to JPEG
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream) // Reduced quality for speed
        val bytes = outputStream.toByteArray()

        // Clean up
        if (rotatedBitmap != bitmap) {
            rotatedBitmap.recycle()
        }
        bitmap.recycle()

        return bytes
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}