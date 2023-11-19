package com.example.vkr_todolist.utils

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.vkr_todolist.R
import pub.devrel.easypermissions.EasyPermissions

object PermissionsUtil {
    private const val GALLERY_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE

    private const val REQUEST_CODE_GALLERY_PERMISSION = 101

    fun hasGalleryPermission(fragment: Fragment): Boolean {
        return EasyPermissions.hasPermissions(fragment.requireContext(), GALLERY_PERMISSION)
    }

    fun requestGalleryPermission(fragment: Fragment, context: Context) {
        EasyPermissions.requestPermissions(
            fragment,
            context.getString(R.string.storage_permission_text),
            REQUEST_CODE_GALLERY_PERMISSION,
            GALLERY_PERMISSION
        )
    }

    private fun onRequestPermissionsResult(
        permissions: Map<String, Boolean>,
        onGalleryPermissionGranted: () -> Unit
    ) {
        if (permissions[GALLERY_PERMISSION] == true) {
            onGalleryPermissionGranted.invoke()
        }
    }

    fun registerForPermissionsResult(fragment: Fragment) =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            PermissionsUtil.onRequestPermissionsResult(
                permissions,
                onGalleryPermissionGranted = {  }
            )
        }
}