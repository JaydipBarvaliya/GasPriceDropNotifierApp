package com.gpn.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gpn.network.Alert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyAlertDialog(
    alert: Alert,
    onDismiss: () -> Unit,
    onUpdate: (Alert) -> Unit,
    onDelete: (Alert) -> Unit // Added delete callback
) {
    var expectedPrice by remember { mutableStateOf(alert.expectedPrice.toString()) }
    val fuelTypes = listOf("Regular", "Midgrade", "Premium", "Diesel", "E85", "UNL88")

    val fuelTypeMap = mapOf(
        "Regular" to 1, "Midgrade" to 2, "Premium" to 3, "Diesel" to 4, "E85" to 5, "UNL88" to 12
    )

    fun getFuelTypeId(fuelTypeName: String): Int {
        return fuelTypeMap[fuelTypeName] ?: 1
    }

    var fuelTypeIndex by remember { mutableIntStateOf(alert.fuelType) }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) } // Track delete confirmation

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modify Alert") },
        text = {
            Column {
                OutlinedTextField(
                    value = expectedPrice,
                    shape = MaterialTheme.shapes.large,
                    onValueChange = { expectedPrice = it },
                    label = { Text("Price Drop Below") }
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = fuelTypesMap[fuelTypeIndex], // Display selected fuel type
                        onValueChange = {},
                        shape = MaterialTheme.shapes.large,
                        readOnly = true, // Prevent manual text input
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        fuelTypes.forEachIndexed { index, type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    fuelTypeIndex = index
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column {
                Button(onClick = {
                    onUpdate(
                        alert.copy(
                            expectedPrice = expectedPrice.toDouble(),
                            fuelType = fuelTypeIndex
                        )
                    )
                }) {
                    Text("Update Alert")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Show confirmation dialog before deleting
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this alert?") },
            confirmButton = {
                Button(onClick = {
                    onDelete(alert)
                    showDeleteDialog = false // Close confirmation dialog
                    onDismiss() // Close ModifyAlertDialog
                }) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
