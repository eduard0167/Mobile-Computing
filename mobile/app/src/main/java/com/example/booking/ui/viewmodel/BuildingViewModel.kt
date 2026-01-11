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
import com.example.booking.data.model.Building
import com.example.booking.data.remote.model.CreateBuildingDto
import com.example.booking.data.repository.BuildingRepository
import kotlinx.coroutines.launch

class BuildingViewModel(private val repository: BuildingRepository) : ViewModel() {

    var buildings = mutableStateListOf<Building>()
        private set

    var searchQuery by mutableStateOf("")

    val filteredBuildings: List<Building>
        get() {
            return if (searchQuery.isBlank()) {
                buildings
            } else {
                buildings.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.university.contains(searchQuery, ignoreCase = true)
                }
            }
        }

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage: String? = null
        private set

    init {
        getBuildings()
    }

    fun getBuildings() {
        viewModelScope.launch {
            val result = repository.getBuildings()

            result
                .onSuccess { list ->
                    buildings.clear()
                    buildings.addAll(list)
                }
                .onFailure { error ->
                    errorMessage = error.localizedMessage
                }
        }
    }

    fun addBuilding(building: CreateBuildingDto) {
        viewModelScope.launch {
            try {
                val result = repository.addBuilding(building)
                result
                    .onSuccess { newBuilding ->
                        newBuilding?.let { buildings.add(it) }
                    }
                    .onFailure { error ->
                        errorMessage = error.localizedMessage
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookingApplication)
                val repository = application.container.buildingRepository
                BuildingViewModel(repository)
            }
        }
    }
}