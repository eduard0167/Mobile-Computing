package com.example.booking.data.remote.model

import com.google.gson.annotations.SerializedName

data class ReservationUpdateDto(
    @SerializedName("status") val status: String? = null,
    @SerializedName("observations") val observations: String? = null
)
