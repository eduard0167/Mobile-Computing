package com.example.booking.data.model

data class Building(
    val id: String,
    val name: String,
    val university: String,
    val description: String? = null,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrls: List<String>
)