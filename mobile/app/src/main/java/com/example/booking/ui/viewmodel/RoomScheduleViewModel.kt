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
import com.example.booking.data.model.Reservation
import com.example.booking.data.repository.ReservationRepository
import com.example.booking.ui.utils.ReservationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RoomScheduleViewModel(
    private val repository: ReservationRepository,
    private val roomId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReservationUiState>(ReservationUiState.Idle)
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    private val _roomReservations = MutableStateFlow<List<Reservation>>(emptyList())
    private val _selectedDate = MutableStateFlow<java.time.LocalDate>(java.time.LocalDate.now())
    val selectedDate: StateFlow<java.time.LocalDate> = _selectedDate.asStateFlow()

    val dailyReservations: StateFlow<List<Reservation>>
        get() = _dailyReservations.asStateFlow()
    
    private val _dailyReservations = MutableStateFlow<List<Reservation>>(emptyList())

    init {
        loadRoomReservations()
        viewModelScope.launch {
        }
    }

    private fun loadRoomReservations() {
        viewModelScope.launch {
            _uiState.value = ReservationUiState.Loading
            repository.getReservationsForRoom(roomId)
                .onSuccess { list ->
                    _roomReservations.value = list
                    updateDailyReservations()
                    _uiState.value = ReservationUiState.Idle
                }
                .onFailure {
                    _uiState.value = ReservationUiState.Error(it.message ?: "Failed to load room schedule")
                }
        }
    }

    fun updateSelectedDate(date: java.time.LocalDate) {
        _selectedDate.value = date
        updateDailyReservations()
    }
    
    private fun updateDailyReservations() {
        val currentList = _roomReservations.value
        val date = _selectedDate.value
        
        val filtered = currentList.filter {
            try {
                val start = LocalDateTime.parse(it.startTime)
                start.toLocalDate() == date
            } catch (e: Exception) {
                false
            }
        }.sortedBy { it.startTime }
        
        _dailyReservations.value = filtered
    }

    companion object {
        fun provideFactory(
            application: BookingApplication,
            roomId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = application.container.reservationRepository
                RoomScheduleViewModel(repository, roomId)
            }
        }
    }
}
