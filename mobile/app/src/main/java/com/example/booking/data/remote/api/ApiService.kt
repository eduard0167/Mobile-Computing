package com.example.booking.data.remote.api

import com.example.booking.data.remote.model.AuthResponse
import com.example.booking.data.remote.model.LoginRequest
import com.example.booking.data.remote.model.RegisterRequest
import com.example.booking.data.remote.model.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signup(@Body request: RegisterRequest): AuthResponse

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): UserDto

    @GET("/users")
    suspend fun fetchRemoteUsers(@Header("Authorization") token: String): List<UserDto>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String)
}

