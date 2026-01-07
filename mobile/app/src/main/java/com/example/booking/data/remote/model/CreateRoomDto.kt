package com.example.booking.data.remote.model

import android.net.Uri

data class CreateRoomDto(
    val name: String,
    val buildingId: Int,
    val capacity: Int,
    val characteristics: String,
    val images: List<Uri> = emptyList(),
)
