package com.example.booking.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booking.ui.utils.ReservationUiState
import com.example.booking.ui.viewmodel.CreateReservationViewModel
import com.example.booking.BookingApplication

import androidx.compose.ui.res.stringResource
import com.example.booking.R

@Composable
fun CreateReservationScreen(
    roomId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as BookingApplication
    val viewModel: CreateReservationViewModel = viewModel(
        factory = CreateReservationViewModel.provideFactory(application, roomId)
    )

    val uiState by viewModel.uiState.collectAsState()

    val event = viewModel.event
    val startTime = viewModel.startTime
    val endTime = viewModel.endTime

    LaunchedEffect(uiState) {
        when (uiState) {
            is ReservationUiState.Success -> {
                Toast.makeText(context, context.getString(R.string.reservation_created_toast), Toast.LENGTH_LONG).show()
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
                Text(text = stringResource(R.string.reserve_room_title_format, roomId), style = MaterialTheme.typography.headlineMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextField(
                    value = event,
                    onValueChange = { viewModel.event = it },
                    label = { Text(stringResource(R.string.event_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = startTime,
                    onValueChange = { viewModel.startTime = it },
                    label = { Text(stringResource(R.string.start_time_label)) },
                    placeholder = { Text(stringResource(R.string.datetime_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = endTime,
                    onValueChange = { viewModel.endTime = it },
                    label = { Text(stringResource(R.string.end_time_label)) },
                    placeholder = { Text(stringResource(R.string.datetime_placeholder)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { 
                        viewModel.createReservation() 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = event.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()
                ) {
                    Text(stringResource(R.string.create_reservation_button))
                }
            }
        }
    }
}
