package com.gpn.ui

import GasPriceViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gpn.FuelTypeDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: GasPriceViewModel) {
    var search by remember { mutableStateOf("L5V2V3") }
    var maxAge by remember { mutableStateOf("0") }
    var selectedFuelType by remember { mutableStateOf("Regular") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Search Input
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Zip Code or City") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Fuel Type Dropdown
        FuelTypeDropdownMenu(
            selectedFuelType = selectedFuelType,
            onFuelTypeSelected = { newType -> selectedFuelType = newType }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hours Input
        OutlinedTextField(
            value = maxAge,
            onValueChange = { maxAge = it },
            label = { Text("Maximum Age (Hours)") },
            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = MaterialTheme.shapes.large,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Search Button
        Button(
            onClick = {
                val maxAgeInt = maxAge.toIntOrNull() ?: 0
                viewModel.fetchStations(
                    search = search,
                    fuel = viewModel.getFuelTypeId(selectedFuelType),
                    maxAge = maxAgeInt
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.large,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text("Search Gas Stations", style = MaterialTheme.typography.labelLarge)
        }
    }
}