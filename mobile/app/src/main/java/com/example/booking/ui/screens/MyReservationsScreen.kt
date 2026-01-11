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
fun MyReservationsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel = viewModel(factory = ReservationViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val myReservations by viewModel.myReservations.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadMyReservations()
    }

    LaunchedEffect(uiState) {
        if (uiState is ReservationUiState.Error) {
             Toast.makeText(context, (uiState as ReservationUiState.Error).message, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState is ReservationUiState.Loading) {
            CircularProgressIndicator()
        } else if (myReservations.isEmpty()) {
             Text(text = "No reservations found.")
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(myReservations.size) { index ->
                    val reservation = myReservations[index]
                    ReservationItem(
                        reservation = reservation,
                        onDelete = { viewModel.deleteReservation(reservation.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservationItem(
    reservation: com.example.booking.data.model.Reservation,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = reservation.event, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Room: ${reservation.room?.name ?: "Unknown Room"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Start: ${reservation.startTime}", style = MaterialTheme.typography.bodySmall)
            Text(text = "End: ${reservation.endTime}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Status: ${reservation.status}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Reservation")
            }
        }
    }
}
