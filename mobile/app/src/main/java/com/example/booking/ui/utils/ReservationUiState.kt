package com.example.booking.ui.utils

sealed class ReservationUiState {
    object Idle : ReservationUiState()
    object Loading : ReservationUiState()
    object Success : ReservationUiState()
    data class Error(val message: String) : ReservationUiState()
}
