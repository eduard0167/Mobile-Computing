package com.example.booking.data.repository

import android.content.Context
import com.example.booking.data.model.Reservation
import com.example.booking.data.remote.api.ReservationService
import com.example.booking.data.remote.model.CreateReservationDto

class ReservationRepository(
    private val api: ReservationService,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString("auth_token", null)
        private set(value) {
            prefs.edit().putString("auth_token", value).apply()
        }

    suspend fun createReservation(reservation: CreateReservationDto): Result<Reservation> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")
            val response = api.createReservation("Bearer $token", reservation)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyReservations(): Result<List<Reservation>> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")
            val userId = prefs.getInt("user_id", -1)
            if (userId == -1) {
                throw Exception("User ID not found. Please log in again.")
            }

            val response = api.getReservationsForUser("Bearer $token", userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReservation(reservationId: Int): Result<Unit> {
         return try {
            val token = authToken ?: throw Exception("Not logged in")
            api.deleteReservation("Bearer $token", reservationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReservationsForRoom(roomId: Int): Result<List<Reservation>> {
        return try {
            val token = authToken ?: throw Exception("Not logged in")
            val response = api.getReservationsForRoom("Bearer $token", roomId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
