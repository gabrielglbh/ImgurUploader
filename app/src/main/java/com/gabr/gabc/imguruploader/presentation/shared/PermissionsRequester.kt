package com.gabr.gabc.imguruploader.presentation.shared

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.util.Calendar

class PermissionsRequester {
    companion object {
        fun getPhotoUri(context: Context): Uri {
            val timeStamp = Calendar.getInstance().timeInMillis.toString()
            val dir =
                File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath}/imguruploader")
            if (!dir.exists()) dir.mkdir()
            val file = File.createTempFile(timeStamp, ".jpg", dir)
            val authority = context.packageName + ".provider"
            return getUriForFile(context, authority, file)
        }

        fun requestMultiplePermissionsCaller(
            c: ComponentActivity,
            pickMedia: ActivityResultLauncher<PickVisualMediaRequest>,
            photoMedia: ActivityResultLauncher<Uri>,
            photoUri: Uri,
        ): ActivityResultLauncher<Array<String>> {
            return c.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.forEach { actionMap ->
                    when (actionMap.key) {
                        Manifest.permission.CAMERA -> {
                            if (actionMap.value) {
                                photoMedia.launch(photoUri)
                            } else {
                                c.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            }
                        }

                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                            if (!actionMap.value) {
                                c.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }

                        Manifest.permission.READ_EXTERNAL_STORAGE -> {
                            if (actionMap.value && !permissions.containsKey(Manifest.permission.CAMERA)) {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            } else {
                                c.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }

                        Manifest.permission.READ_MEDIA_IMAGES -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (actionMap.value && !permissions.containsKey(Manifest.permission.CAMERA)) {
                                    pickMedia.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                } else {
                                    c.shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}