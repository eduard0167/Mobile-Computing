package com.example.booking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booking.BookingApplication
import com.example.booking.data.model.Reservation
import com.example.booking.ui.utils.ReservationUiState
import com.example.booking.ui.viewmodel.RoomScheduleViewModel
import com.example.booking.R
import androidx.compose.ui.res.stringResource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun RoomScheduleScreen(
    roomId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as BookingApplication
    val viewModel: RoomScheduleViewModel = viewModel(
        factory = RoomScheduleViewModel.provideFactory(application, roomId)
    )

    val uiState by viewModel.uiState.collectAsState()
    val dailyReservations by viewModel.dailyReservations.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()


    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.updateSelectedDate(selectedDate.minusDays(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.previous_day_desc))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = { viewModel.updateSelectedDate(selectedDate.plusDays(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.next_day_desc))
            }
        }

        if (uiState is ReservationUiState.Loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            DailyTimeline(reservations = dailyReservations)
        }
    }
}

@Composable
fun DailyTimeline(reservations: List<Reservation>) {
    val startHour = 8
    val endHour = 22
    val hours = (startHour..endHour).toList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(hours.size) { index ->
            val hour = hours[index]
            val timeLabel = String.format("%02d:00", hour)
            
            val matchingReservations = reservations.filter {
                val start = LocalDateTime.parse(it.startTime)
                start.hour == hour
            }

            TimelineHourSlot(timeLabel = timeLabel, reservations = matchingReservations)
        }
    }
}

@Composable
fun TimelineHourSlot(timeLabel: String, reservations: List<Reservation>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Time Column
        Text(
            text = timeLabel,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .width(50.dp)
                .padding(top = 8.dp)
        )

        // Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.LightGray)
        )
        
        // Content Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = 12.dp)
        ) {
            // Horizontal line for the hour mark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            if (reservations.isEmpty()) {
                Spacer(modifier = Modifier.height(48.dp))
            } else {
                reservations.forEach { res ->
                    val start = LocalDateTime.parse(res.startTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    val end = LocalDateTime.parse(res.endTime).format(DateTimeFormatter.ofPattern("HH:mm"))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = res.event, 
                                style = MaterialTheme.typography.titleSmall, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$start - $end",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = stringResource(
                                    R.string.booked_by_format,
                                    res.user?.firstName ?: stringResource(R.string.unknown_user)
                                ),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
