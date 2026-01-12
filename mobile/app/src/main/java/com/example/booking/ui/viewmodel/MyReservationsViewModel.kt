package com.example.booking.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booking.BookingApplication
import com.example.booking.data.model.Reservation
import com.example.booking.data.repository.ReservationRepository
import com.example.booking.ui.utils.ReservationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyReservationsViewModel(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservationUiState>(ReservationUiState.Idle)
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    private val _myReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val myReservations: StateFlow<List<Reservation>> = _myReservations.asStateFlow()

    init {
        loadMyReservations()
    }

    private fun loadMyReservations() {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            repository.getMyReservations()
                .onSuccess { list ->
                    _myReservations.value = list
                    _uiState.value = ReservationUiState.Idle
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to load reservations")
                }
        }
    }

    fun deleteReservation(reservationId: Int) {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            repository.deleteReservation(reservationId)
                .onSuccess {
                    loadMyReservations()
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to delete reservation")
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val repository = application.container.reservationRepository
                MyReservationsViewModel(repository)
            }
        }
    }
}
