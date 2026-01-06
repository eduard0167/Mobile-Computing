package com.example.booking.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booking.data.model.Building
import com.example.booking.ui.utils.BuildingCard
import com.example.booking.ui.utils.SearchScreen
import com.example.booking.ui.viewmodel.BuildingViewModel

@Composable
fun SearchBuildingScreen(
    onBuildingSelected: (Int) -> Unit,
    viewModel: BuildingViewModel = viewModel(factory = BuildingViewModel.Factory)
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddModal by remember { mutableStateOf(false) }

    val buildings = viewModel.buildings
    val filteredBuildings = buildings.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.university.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        viewModel.getBuildings()
    }

    SearchScreen(
        title = "Select Building",
        searchQuery = searchQuery,
        onSearchQueryChange = { searchQuery = it },
        items = filteredBuildings,
        emptyText = "No buildings found",
        onAddClick = { showAddModal = true },
        itemContent = { building: Building ->
            BuildingCard(
                building = building,
                onClick = { onBuildingSelected(building.id.toInt()) }
            )
        }
    )

    if (showAddModal) {
        AddBuildingBottomSheet(
            onDismiss = { showAddModal = false },
            onSave = { newBuilding ->
                viewModel.addBuilding(newBuilding)
                showAddModal = false
            }
        )
    }
}