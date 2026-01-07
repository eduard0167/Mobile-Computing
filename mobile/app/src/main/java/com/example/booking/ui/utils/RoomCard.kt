package com.example.booking.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booking.BuildConfig
import com.example.booking.data.model.Room
import com.example.booking.utils.joinPath
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RoomCard(
    room: Room,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = room.name,
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Capacity",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${room.capacity} people",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val characteristics = room.characteristics
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (characteristics.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    characteristics.forEach { item ->
                        AssistChip(
                            onClick = {},
                            label = { Text(item) }
                        )
                    }
                }
            }

            if (room.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                val pagerState = rememberPagerState()

                Column {
                    HorizontalPager(
                        state = pagerState,
                        count = room.imageUrls.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        key = { room.imageUrls[it] }
                    ) { page ->
                        AsyncImage(
                            model = BuildConfig.BASE_URL.joinPath(room.imageUrls[page]),
                            contentDescription = room.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp),
                        activeColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
