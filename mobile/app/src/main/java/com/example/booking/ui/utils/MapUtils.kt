package com.example.booking.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openMap(context: Context, latitude: Double, longitude: Double, label: String? = null) {
    val geoUri = if (!label.isNullOrEmpty()) {
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
    } else {
        Uri.parse("geo:$latitude,$longitude")
    }

    val intent = Intent(Intent.ACTION_VIEW, geoUri)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
        )
        context.startActivity(browserIntent)
    }
}