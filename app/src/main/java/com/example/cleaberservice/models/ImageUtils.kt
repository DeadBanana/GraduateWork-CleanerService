package com.example.cleaberservice.models

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {
    private const val CAMERA_PERMISSION_REQUEST = 100
    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun getPhotoUri(context: Context): Uri? {
        val photoFile: File? = try {
            createImageFile(context)
        } catch (ex: IOException) {
            null
        }
        return photoFile?.let {
            FileProvider.getUriForFile(
                context,
                "com.example.cleaberservice.fileprovider",
                it
            )
        }
    }

    fun getCurrentPhotoPath(): String? {
        return currentPhotoPath
    }

    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(fragment: Fragment) {
        fragment.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
    }

    fun handleCameraPermissionResult(
        requestCode: Int,
        grantResults: IntArray,
        onGranted: () -> Unit
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            // Handle permission denial if needed
        }
    }

    fun takeImage(fragment: Fragment, requestCode: Int): Uri? {
        val context = fragment.requireContext()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoUri = getPhotoUri(context)
        photoUri?.let {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, it)
            fragment.startActivityForResult(takePictureIntent, requestCode)
        }
        return photoUri
    }

    fun handleImageCaptureResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        expectedRequestCode: Int,
        onImageCaptured: (Uri?) -> Unit
    ) {
        if (requestCode == expectedRequestCode && resultCode == Activity.RESULT_OK) {
            onImageCaptured(getPhotoUriFromPath())
        }
    }

    private fun getPhotoUriFromPath(): Uri? {
        return currentPhotoPath?.let { Uri.parse(it) }
    }
}