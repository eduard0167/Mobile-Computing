package com.example.booking

import android.content.Context
import androidx.room.Room
import com.example.booking.data.local.AppDatabase
import com.example.booking.data.remote.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.booking.data.repository.UserRepository

interface AppContainer {
    val userRepository: UserRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "http://10.0.2.2:8000/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "upbooking_db"
        ).build()
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(retrofitService, database.userDao(), context)
    }
}