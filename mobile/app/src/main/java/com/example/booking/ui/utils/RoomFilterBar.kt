package com.example.booking.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val ROOM_CHARACTERISTICS = listOf("Projector", "Whiteboard", "TV")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomFilterBar(
    minCapacity: Int,
    onMinCapacityChange: (Int) -> Unit,
    selectedCharacteristics: Set<String>,
    onCharacteristicChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Filters"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Advanced filters",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = if (minCapacity == 0) "" else minCapacity.toString(),
                        onValueChange = {
                            onMinCapacityChange(it.toIntOrNull() ?: 0)
                        },
                        label = { Text("Cap.") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        modifier = Modifier
                            .width(90.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ROOM_CHARACTERISTICS.forEach { characteristic ->
                            FilterChip(
                                selected = selectedCharacteristics.contains(characteristic),
                                onClick = { onCharacteristicChange(characteristic) },
                                label = {
                                    Text(
                                        text = characteristic,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}
