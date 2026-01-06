package com.example.booking.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booking.data.remote.model.CreateRoomDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomSheet(
    buildingId: Int,
    onDismiss: () -> Unit,
    onSave: (room: CreateRoomDto) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var capacity by remember { mutableIntStateOf(1) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val characteristics = remember {
        mutableStateMapOf(
            "Projector" to false,
            "Whiteboard" to false,
            "TV" to false
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = uris
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Add Room",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Room name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Room name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Capacity", style = MaterialTheme.typography.labelMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (capacity > 1) capacity-- }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease capacity")
                }

                OutlinedTextField(
                    value = capacity.toString(),
                    onValueChange = {
                        capacity = it.toIntOrNull()?.coerceAtLeast(1) ?: capacity
                    },
                    modifier = Modifier.width(80.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center
                    )
                )

                IconButton(
                    onClick = { capacity++ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase capacity")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Characteristics", style = MaterialTheme.typography.labelMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                characteristics.forEach { (label, checked) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            characteristics[label] = !checked
                        }
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { characteristics[label] = it }
                        )
                        Text(text = label)
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            // Image picker
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Select Images")
            }

            if (selectedImages.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedImages) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val characteristicsString = characteristics
                            .filterValues { it }
                            .keys
                            .joinToString(",")

                        onSave(
                            CreateRoomDto(
                                name = name,
                                buildingId = buildingId,
                                capacity = capacity,
                                characteristics = characteristicsString,
                                images = selectedImages
                            )
                        )
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}

