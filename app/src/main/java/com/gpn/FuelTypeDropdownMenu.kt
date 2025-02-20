package com.gpn

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeDropdownMenu(
    selectedFuelType: String,
    onFuelTypeSelected: (String) -> Unit
) {
    val fuelTypes = listOf("Regular", "Midgrade", "Premium", "Diesel", "E85", "UNL88")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedFuelType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fuel Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = { Icon(Icons.Default.LocalGasStation, contentDescription = null) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            fuelTypes.forEach { fuelType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = fuelType,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        onFuelTypeSelected(fuelType)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when(fuelType) {
                                "Diesel" -> Icons.Default.DirectionsCar
                                else -> Icons.Default.FlashOn
                            },
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}