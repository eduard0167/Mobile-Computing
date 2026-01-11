package com.example.booking.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booking.BookingApplication
import com.example.booking.data.remote.model.CreateReservationDto
import com.example.booking.data.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReservationUiState {
    object Idle : ReservationUiState()
    object Loading : ReservationUiState()
    object Success : ReservationUiState()
    data class Error(val message: String) : ReservationUiState()
}

class ReservationViewModel(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservationUiState>(ReservationUiState.Idle)
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    private val _myReservations = MutableStateFlow<List<com.example.booking.data.model.Reservation>>(emptyList())
    val myReservations: StateFlow<List<com.example.booking.data.model.Reservation>> = _myReservations.asStateFlow()

    private val _roomReservations = MutableStateFlow<List<com.example.booking.data.model.Reservation>>(emptyList())
    val roomReservations: StateFlow<List<com.example.booking.data.model.Reservation>> = _roomReservations.asStateFlow()

    private val _selectedDate = MutableStateFlow<java.time.LocalDate>(java.time.LocalDate.now())
    val selectedDate: StateFlow<java.time.LocalDate> = _selectedDate.asStateFlow()

    fun updateSelectedDate(date: java.time.LocalDate) {
        _selectedDate.value = date
    }

    fun createReservation(roomId: Int, event: String, startTime: String, endTime: String) {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            
            val dto = CreateReservationDto(roomId, event, startTime, endTime)
            val result = reservationRepository.createReservation(dto)
            
            result.onSuccess {
                _uiState.value = ReservationUiState.Success
            }.onFailure {
                _uiState.value = ReservationUiState.Error(it.message ?: "Unknown error")
            }
        }
    }
    
    fun loadMyReservations() {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            reservationRepository.getMyReservations()
                .onSuccess { list ->
                    _myReservations.value = list
                    _uiState.value = ReservationUiState.Idle
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to load reservations")
                }
        }
    }

    fun loadRoomReservations(roomId: Int) {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            reservationRepository.getReservationsForRoom(roomId)
                .onSuccess { list ->
                    _roomReservations.value = list
                    _uiState.value = ReservationUiState.Idle
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to load room schedule")
                }
        }
    }

    fun deleteReservation(reservationId: Int) {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            reservationRepository.deleteReservation(reservationId)
                .onSuccess {
                    loadMyReservations()
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to delete reservation")
                }
        }
    }

    fun resetState() {
        _uiState.value = ReservationUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val repository = application.container.reservationRepository
                ReservationViewModel(repository)
            }
        }
    }
}
