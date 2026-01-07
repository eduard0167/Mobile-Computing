package com.example.booking.data.remote.api

import com.example.booking.data.model.Building
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BuildingService {
    @Multipart
    @POST("/buildings/")
    suspend fun createBuilding(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("university") university: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part images: List<MultipartBody.Part> = emptyList()
    ): Response<Building>

    @GET("/buildings/")
    suspend fun getBuildings(@Header("Authorization") token: String): Response<List<Building>>
}