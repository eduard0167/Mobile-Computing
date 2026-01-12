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
import androidx.compose.ui.platform.LocalContext
import com.example.booking.BookingApplication

import androidx.compose.ui.res.stringResource
import com.example.booking.R

@Composable
fun SearchRoomScreen(
    buildingId: Int,
    onRoomSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as BookingApplication
    val viewModel: RoomViewModel = viewModel(
        factory = RoomViewModel.provideFactory(application, buildingId)
    )

    var showAddModal by remember { mutableStateOf(false) }

    val filteredRooms = viewModel.filteredRooms
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val emptyText = when {
        errorMessage != null -> stringResource(R.string.error_occurred_prefix, errorMessage)
        else -> stringResource(R.string.no_rooms_found)
    }

    SearchScreen(
        title = stringResource(R.string.select_room_title),
        searchQuery = viewModel.searchQuery,
        onSearchQueryChange = { viewModel.searchQuery = it },
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
                minCapacity = viewModel.minCapacity,
                onMinCapacityChange = { viewModel.minCapacity = it },
                selectedCharacteristics = viewModel.selectedCharacteristics,
                onCharacteristicChange = { characteristic ->
                    viewModel.selectedCharacteristics =
                        if (viewModel.selectedCharacteristics.contains(characteristic)) {
                            viewModel.selectedCharacteristics - characteristic
                        } else {
                            viewModel.selectedCharacteristics + characteristic
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
