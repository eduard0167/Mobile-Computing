package com.example.booking.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    val faculty: String?,
    val year: String?
)
