package com.example.booking.data.model

data class Room(
    val id: Int,
    val name: String,
    val buildingId: Int,
    val capacity: Int,
    val characteristics: String,
    val imageUrls: List<String> = emptyList(),
)