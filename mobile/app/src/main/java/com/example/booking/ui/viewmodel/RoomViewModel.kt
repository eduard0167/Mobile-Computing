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
    private val buildingId: Int
) : ViewModel() {

    var rooms = mutableStateListOf<Room>()
        private set

    var searchQuery by mutableStateOf("")
    var minCapacity by mutableStateOf(0)
    var selectedCharacteristics by mutableStateOf(setOf<String>())

    val filteredRooms: List<Room>
        get() {
            return rooms.filter { room ->
                val nameOk = room.name.contains(searchQuery, ignoreCase = true)
                val capacityOk = room.capacity >= minCapacity

                val roomCharacteristics = room.characteristics
                    .split(",")
                    .map { it.trim() }
                    .toSet()
                val characteristicsOk = selectedCharacteristics.all { it in roomCharacteristics }

                nameOk && capacityOk && characteristicsOk
            }
        }

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        getRooms()
    }

    private fun getRooms() {
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
                    .onSuccess { newRoom ->
                        newRoom.let { rooms.add(it) }
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
        fun provideFactory(
            application: BookingApplication,
            buildingId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = application.container.roomRepository
                RoomViewModel(repository, buildingId)
            }
        }
    }
}