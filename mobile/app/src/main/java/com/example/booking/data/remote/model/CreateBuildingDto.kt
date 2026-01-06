package com.example.booking.data.remote.model

import android.net.Uri

data class CreateBuildingDto(
    val name: String,
    val university: String,
    val description: String? = null,
    val address: String,
    val latitude: Float,
    val longitude: Float,
    val images: List<Uri> = emptyList()
)