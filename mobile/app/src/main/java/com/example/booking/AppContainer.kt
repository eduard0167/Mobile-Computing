package com.example.booking

import android.content.Context
import com.example.booking.data.local.AppDatabase
import com.example.booking.data.remote.api.ApiService
import com.example.booking.data.remote.api.BuildingService
import com.example.booking.data.remote.api.RoomService
import com.example.booking.data.repository.BuildingRepository
import com.example.booking.data.repository.RoomRepository
import com.example.booking.data.repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val userRepository: UserRepository

    val buildingRepository: BuildingRepository

    val roomRepository: RoomRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private val buildingService: BuildingService by lazy {
        retrofit.create(BuildingService::class.java)
    }

    private val roomService: RoomService by lazy {
        retrofit.create(RoomService::class.java)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(retrofitService, database.userDao(), context)
    }

    override val buildingRepository: BuildingRepository by lazy {
        BuildingRepository(buildingService, context)
    }

    override val roomRepository: RoomRepository by lazy {
        RoomRepository(roomService, context)
    }
}