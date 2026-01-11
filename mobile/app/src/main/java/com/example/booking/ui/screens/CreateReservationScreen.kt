package com.example.booking.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booking.ui.viewmodel.ReservationUiState
import com.example.booking.ui.viewmodel.ReservationViewModel

@Composable
fun CreateReservationScreen(
    roomId: Int,
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel = viewModel(factory = ReservationViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var event by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("2023-10-27T10:00:00") }
    var endTime by remember { mutableStateOf("2023-10-27T12:00:00") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ReservationUiState.Success -> {
                Toast.makeText(context, "Reservation Created!", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is ReservationUiState.Error -> {
                Toast.makeText(context, (uiState as ReservationUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState is ReservationUiState.Loading) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Reserve Room $roomId", style = MaterialTheme.typography.headlineMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = event,
                    onValueChange = { event = it },
                    label = { Text("Event Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time (ISO 8601)") },
                    placeholder = { Text("YYYY-MM-DDTHH:MM:SS") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time (ISO 8601)") },
                    placeholder = { Text("YYYY-MM-DDTHH:MM:SS") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { 
                        viewModel.createReservation(roomId, event, startTime, endTime) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = event.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
                ) {
                    Text("Create Reservation")
                }
            }
        }
    }
}
