package com.example.dogcatandi.presentation.utils.extensions

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showRetryDialog(
    message: String,
    onRetry: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle("เกิดข้อผิดพลาด")
        .setMessage(message)
        .setPositiveButton("ลองใหม่") { _, _ -> onRetry() }
        .setNegativeButton("ยกเลิก", null)
        .show()
}