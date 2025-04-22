package com.example.dogcatandi.presentation.utils.extensions

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.showShortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun View.visible() = apply { visibility = View.VISIBLE }
fun View.gone() = apply { visibility = View.GONE }
