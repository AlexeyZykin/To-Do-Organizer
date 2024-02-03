package com.example.vkr_todolist.presentation.dialogs

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionsDialog {
    fun showPermissionSettingsDialog(fragment: Fragment) {
        val builder = AlertDialog.Builder(requireNotNull(fragment.activity))
        builder.setTitle("Permission Required")
        builder.setMessage("This app needs permission to access your media files. Please grant access in app settings.")
        builder.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
            intent.data = uri
            ContextCompat.startActivity(fragment.requireContext(), intent, null)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}