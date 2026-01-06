package com.example.booking.data.repository

import android.content.Context
import com.example.booking.data.model.Building
import com.example.booking.data.remote.api.BuildingService
import com.example.booking.data.remote.model.CreateBuildingDto
import com.example.booking.utils.uriToMultipart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class BuildingRepository(
    private val api: BuildingService,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        private set(value) {
            prefs.edit().putString("auth_token", value).apply()
        }

    suspend fun addBuilding(building: CreateBuildingDto): Result<Building?> {
        return try {
            val imageParts = building.images.mapIndexedNotNull { index, uri ->
                uriToMultipart(context, uri, "images", "image_$index.jpg")
            }

            val response = api.createBuilding(
                name = building.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                university = building.university.toRequestBody("text/plain".toMediaTypeOrNull()),
                description = building.description?.toRequestBody("text/plain".toMediaTypeOrNull()),
                address = building.address.toRequestBody("text/plain".toMediaTypeOrNull()),
                latitude = building.latitude.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                longitude = building.longitude.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                images = imageParts
            )

            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(
                    Exception(response.errorBody()?.string() ?: "Unknown error")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBuildings(): Result<List<Building>> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")
            val response = api.getBuildings("Bearer $token")

            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(
                    Exception(response.errorBody()?.string() ?: "Unknown error")
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}