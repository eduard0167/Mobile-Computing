package com.example.booking.data.remote.api

import com.example.booking.data.model.Reservation
import com.example.booking.data.remote.model.CreateReservationDto
import com.example.booking.data.remote.model.ReservationUpdateDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationService {
    @POST("reservations")
    suspend fun createReservation(
        @Header("Authorization") token: String,
        @Body request: CreateReservationDto
    ): Reservation

    @GET("reservations/user/{userId}")
    suspend fun getReservationsForUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): List<Reservation>

    @GET("reservations/room/{roomId}")
    suspend fun getReservationsForRoom(
        @Header("Authorization") token: String,
        @Path("roomId") roomId: Int
    ): List<Reservation>

    @PATCH("reservations/{reservationId}")
    suspend fun updateReservation(
        @Header("Authorization") token: String,
        @Path("reservationId") reservationId: Int,
        @Body request: ReservationUpdateDto
    ): Reservation

    @DELETE("reservations/{reservationId}")
    suspend fun deleteReservation(
        @Header("Authorization") token: String,
        @Path("reservationId") reservationId: Int
    ): Any
}
