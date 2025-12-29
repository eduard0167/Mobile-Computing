package com.example.booking.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    val faculty: String?,
    val year: String?,
    val role: String = "STUDENT"
)

