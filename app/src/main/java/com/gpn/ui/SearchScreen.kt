package com.gpn.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpn.FuelTypeDropdownMenu

@Composable
fun SearchScreen(viewModel: GasPriceViewModel) {
    // Local states for the zip code/city and maxAge
    var search by remember { mutableStateOf("L5V2V3") }
    var maxAge by remember { mutableStateOf("0") }

    Column(modifier = Modifier) {
        // ZIP Code or City
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Zip Code or City") },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fuel Type (using the ExposedDropdownMenu)
        FuelTypeDropdownMenu()

        Spacer(modifier = Modifier.height(16.dp))

        // maxAge (prices updated in last X hours)
        OutlinedTextField(
            value = maxAge,
            onValueChange = { maxAge = it },
            label = { Text("Prices Updated in Last (Hours)") },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = {
                // Trigger your API call here using viewModel
                // Convert maxAge to Int if needed
                val maxAgeInt = maxAge.toIntOrNull() ?: 0
                // Provide search, fuel, maxAge to your fetch function
                // For now, just a placeholder
            }
        ) {
            Text("Search Gas Stations")
        }
    }
}
