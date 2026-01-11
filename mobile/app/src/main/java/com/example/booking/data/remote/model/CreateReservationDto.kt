package com.example.booking.data.remote.model

import com.google.gson.annotations.SerializedName

data class CreateReservationDto(
    @SerializedName("roomId") val roomId: Int,
    @SerializedName("event") val event: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)
