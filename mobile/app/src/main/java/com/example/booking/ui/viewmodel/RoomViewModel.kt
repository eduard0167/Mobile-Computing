package com.example.booking.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.booking.BookingApplication
import com.example.booking.data.model.Room
import com.example.booking.data.remote.model.CreateRoomDto
import com.example.booking.data.repository.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel(
    private val repository: RoomRepository,
) : ViewModel() {

    var rooms = mutableStateListOf<Room>()
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun getRooms(buildingId: Int) {
        viewModelScope.launch {
            errorMessage = null
            try {
                val result: Result<List<Room>> = repository.getRoomsFromBuilding(buildingId)
                val roomsList = result.getOrThrow()
                rooms.clear()
                rooms.addAll(roomsList)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun addRoom(room: CreateRoomDto) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = repository.createRoom(room)

                result
                    .onSuccess { newBuilding ->
                        newBuilding.let { rooms.add(it) }
                    }
                    .onFailure { error ->
                        errorMessage = error.localizedMessage
                    }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val repository = application.container.roomRepository
                RoomViewModel(repository)
            }
        }
    }
}