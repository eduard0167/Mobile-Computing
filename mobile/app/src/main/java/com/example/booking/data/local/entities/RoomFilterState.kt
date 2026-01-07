package com.example.booking.data.local.entities

data class RoomFilterState(
    val minCapacity: Int = 1,
    val selectedCharacteristics: Set<String> = emptySet()
)
