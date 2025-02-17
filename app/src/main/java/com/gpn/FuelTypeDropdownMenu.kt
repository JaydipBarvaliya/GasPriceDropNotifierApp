package com.gpn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeDropdownMenu() {
    val fuelTypes = listOf("Regular", "Midgrade", "Premium", "Diesel", "E85", "UNL88")
    var expanded by remember { mutableStateOf(false) }
    var selectedFuelType by remember { mutableStateOf(fuelTypes[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Text(
            text = selectedFuelType,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            fuelTypes.forEach { fuelType ->
                DropdownMenuItem(
                    text = { Text(fuelType) },
                    onClick = {
                        selectedFuelType = fuelType
                        expanded = false
                    }
                )
            }
        }
    }
}
