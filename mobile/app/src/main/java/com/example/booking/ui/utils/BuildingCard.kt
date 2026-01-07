package com.example.booking.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booking.BuildConfig
import com.example.booking.data.model.Building
import com.example.booking.utils.joinPath
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@Composable
fun BuildingCard(
    building: Building,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = building.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = building.university,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    openMap(context, building.latitude, building.longitude, building.name)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Open in Maps",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = building.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (building.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                val pagerState = rememberPagerState()

                Column {
                    HorizontalPager(
                        state = pagerState,
                        count = building.imageUrls.size,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        key = { building.imageUrls[it] }
                    ) { page ->
                        AsyncImage(
                            model = BuildConfig.BASE_URL.joinPath(building.imageUrls[page]),
                            contentDescription = building.name,
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
