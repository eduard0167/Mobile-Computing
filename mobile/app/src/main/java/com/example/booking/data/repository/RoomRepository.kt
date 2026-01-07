package com.example.booking.data.repository

import android.content.Context
import com.example.booking.data.model.Room
import com.example.booking.data.remote.api.RoomService
import com.example.booking.data.remote.model.CreateRoomDto
import com.example.booking.utils.uriToMultipart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class RoomRepository(
    private val api: RoomService,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        private set(value) {
            prefs.edit().putString("auth_token", value).apply()
        }

    suspend fun getRoomsFromBuilding(buildingId: Int): Result<List<Room>> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")

            val response: Response<List<Room>> =
                api.getRoomsFromBuilding("Bearer $token", buildingId)
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

    suspend fun createRoom(room: CreateRoomDto): Result<Room> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")

            val imageParts = room.images.mapIndexedNotNull { index, uri ->
                uriToMultipart(context, uri, "images", "image_$index.jpg")
            }

            val response = api.createRoom(
                "Bearer $token",
                name = room.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                buildingId = room.buildingId.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                capacity = room.capacity.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                characteristics = room.characteristics.toRequestBody("text/plain".toMediaTypeOrNull()),
                images = imageParts
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
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