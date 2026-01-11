package com.example.booking.data.model

data class Reservation(
    val id: Int,
    val userId: Int,
    val roomId: Int,
    val event: String,
    val startTime: String, // Keep as String for now, ISO format
    val endTime: String,
    val status: String,
    val observations: String?,
    val room: Room?,
    val createdAt: String,
    val updatedAt: String
)
