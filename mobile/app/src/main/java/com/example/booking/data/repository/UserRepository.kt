package com.example.booking.data.repository

import android.content.Context
import com.example.booking.data.local.dao.UserDao
import com.example.booking.data.local.entities.UserEntity
import com.example.booking.data.remote.api.ApiService
import com.example.booking.data.remote.model.LoginRequest
import com.example.booking.data.remote.model.RegisterRequest
import com.example.booking.data.remote.model.UserDto
import kotlinx.coroutines.flow.Flow

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

    suspend fun login(request: LoginRequest) {
        val response = api.login(request)
        authToken = response.accessToken
    }

    suspend fun signup(request: RegisterRequest) {
        val response = api.signup(request)
        authToken = response.accessToken
    }

    suspend fun getMe(): UserDto {
        val token = authToken ?: throw Exception("Not logged in")
        val userDto = api.getMe("Bearer $token")
        
        // Save user ID for other repositories to use
        prefs.edit().putInt("user_id", userDto.id).apply()
        
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