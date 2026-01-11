package com.example.booking.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booking.BookingApplication
import com.example.booking.data.remote.model.CreateReservationDto
import com.example.booking.data.repository.ReservationRepository
import com.example.booking.ui.utils.ReservationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateReservationViewModel(
    private val repository: ReservationRepository,
    private val roomId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservationUiState>(ReservationUiState.Idle)
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    var event by mutableStateOf("")
    var startTime by mutableStateOf("2023-10-27T10:00:00")
    var endTime by mutableStateOf("2023-10-27T12:00:00")

    fun createReservation() {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            
            val dto = CreateReservationDto(roomId, event, startTime, endTime)
            val result = repository.createReservation(dto)
            
            result.onSuccess {
                _uiState.value = ReservationUiState.Success
            }.onFailure {
                _uiState.value = ReservationUiState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _uiState.value = ReservationUiState.Idle
    }

    companion object {
        fun provideFactory(
            application: BookingApplication,
            roomId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = application.container.reservationRepository
                CreateReservationViewModel(repository, roomId)
            }
        }
    }
}
