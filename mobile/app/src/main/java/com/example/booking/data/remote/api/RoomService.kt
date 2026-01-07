package com.example.booking.data.remote.api

import com.example.booking.data.model.Room
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RoomService {

    @Multipart
    @POST("/rooms/")
    suspend fun createRoom(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("buildingId") buildingId: RequestBody,
        @Part("capacity") capacity: RequestBody,
        @Part("characteristics") characteristics: RequestBody,
        @Part images: List<MultipartBody.Part> = emptyList()
    ): Response<Room>

    @GET("/rooms/{id}")
    suspend fun getRoomsFromBuilding(
        @Header("Authorization") token: String,
        @Path("id") buildingId: Int
    ): Response<List<Room>>
}