package com.example.booking.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booking.data.model.Room
import com.example.booking.ui.utils.RoomCard
import com.example.booking.ui.utils.RoomFilterBar
import com.example.booking.ui.utils.SearchScreen
import com.example.booking.ui.viewmodel.RoomViewModel

@Composable
fun SearchRoomScreen(
    buildingId: Int,
    onRoomSelected: (Int) -> Unit,
    viewModel: RoomViewModel = viewModel(factory = RoomViewModel.Factory)
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }

    var minCapacity by remember { mutableStateOf(0) }
    var selectedCharacteristics by remember { mutableStateOf(setOf<String>()) }

    val rooms = viewModel.rooms
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val filteredRooms = remember(rooms.size, minCapacity, selectedCharacteristics, searchQuery) {
        rooms.filter { room ->
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

    LaunchedEffect(Unit) {
        viewModel.getRooms(buildingId)
    }

    val emptyText = when {
        errorMessage != null -> "An error occurred: $errorMessage"
        else -> "No rooms found"
    }

    SearchScreen(
        title = "Select Room",
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        items = filteredRooms,
        emptyText = emptyText,
        onAddClick = { showAddModal = true },
        itemContent = { room: Room ->
            RoomCard(
                room = room,
                onClick = { onRoomSelected(room.id) }
            )
        },
        header = {
            RoomFilterBar(
                minCapacity = minCapacity,
                onMinCapacityChange = { minCapacity = it },
                selectedCharacteristics = selectedCharacteristics,
                onCharacteristicChange = { characteristic ->
                    selectedCharacteristics =
                        if (selectedCharacteristics.contains(characteristic)) {
                            selectedCharacteristics - characteristic
                        } else {
                            selectedCharacteristics + characteristic
                        }
                }
            )
        },
        isLoading = isLoading
    )

    if (showAddModal) {
        AddRoomSheet(
            buildingId = buildingId,
            onDismiss = { showAddModal = false },
            onSave = { newRoom ->
                viewModel.addRoom(newRoom)
            }
        )
    }
}
