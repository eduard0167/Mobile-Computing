package com.example.booking.data.repository

import com.example.booking.data.local.dao.UserDao
import com.example.booking.data.local.entities.UserEntity
import com.example.booking.data.remote.api.ApiService
import com.example.booking.data.remote.model.AuthResponse
import com.example.booking.data.remote.model.LoginRequest
import com.example.booking.data.remote.model.RegisterRequest
import com.example.booking.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

import android.content.Context

class UserRepository(
    private val api: ApiService,
    private val dao: UserDao,
    context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        private set(value) {
            prefs.edit().putString("auth_token", value).apply()
        }

    val users: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun login(request: LoginRequest): AuthResponse {
        val response = api.login(request)
        authToken = response.accessToken
        return response
    }

    suspend fun signup(request: RegisterRequest): AuthResponse {
        val response = api.signup(request)
        authToken = response.accessToken
        return response
    }

    suspend fun getMe(): UserDto {
        val token = authToken ?: throw Exception("Not logged in")
        val userDto = api.getMe("Bearer $token")
        val userEntity = userDto.toEntity()
        dao.insertAll(listOf(userEntity))
        return userDto
    }

    suspend fun logout() {
        val token = authToken
        if (token != null) {
            api.logout("Bearer $token")
        }
        dao.deleteAll()
        authToken = null
    }
}

