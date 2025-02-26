package com.gpn.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpn.viewmodel.GasPriceViewModel

@Composable
fun SearchScreen(viewModel: GasPriceViewModel) {
    var search by remember { mutableStateOf("L5V2V3") }
    var maxAge by remember { mutableIntStateOf(0) } // Default to "No Limit"
    var selectedFuelType by remember { mutableStateOf("Regular") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 🔍 Search Input Field
        OutlinedTextField(
            value = search,
            shape = MaterialTheme.shapes.large,
            onValueChange = { search = it },
            label = { Text("Zip Code or City") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ⛽ Fuel Type Dropdown
        FuelTypeDropdownMenu(
            selectedFuelType = selectedFuelType,
            onFuelTypeSelected = { selectedFuelType = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🏪 Brand Selection Dropdown
        BrandDropdown(viewModel)

        Spacer(modifier = Modifier.height(24.dp))

        // ⏳ Max Age Dropdown
        MaxAgeDropdown(selectedAge = maxAge, onAgeSelected = { maxAge = it })

        Spacer(modifier = Modifier.height(32.dp))

        // 🔎 Search Button
        Button(
            onClick = {
                val maxAgeInt = maxAge.toString().toIntOrNull() ?: 0
                viewModel.fetchStations(
                    search = search.trim(),
                    fuel = viewModel.getFuelTypeId(selectedFuelType),
                    maxAge = maxAgeInt
                )
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Search Gas Stations")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelTypeDropdownMenu( selectedFuelType: String, onFuelTypeSelected: (String) -> Unit) {
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
            colors = OutlinedTextFieldDefaults.colors(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDropdown(viewModel: GasPriceViewModel) {
    val brands by viewModel.brands.collectAsStateWithLifecycle()
    var selectedBrand by remember { mutableStateOf("Select a brand") }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedBrand,
                onValueChange = {},
                readOnly = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                brands.forEach { brand ->
                    DropdownMenuItem(
                        text = { Text(brand.name) },
                        onClick = {
                            selectedBrand = brand.name
                            viewModel.setSelectedBrand(brand)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaxAgeDropdown(selectedAge: Int, onAgeSelected: (Int) -> Unit) {
    val options = listOf(0, 4, 8, 12, 24, 36, 48)
    val optionLabels = listOf("No Limit", "4 Hours", "8 Hours", "12 Hours", "24 Hours", "36 Hours", "48 Hours")

    var expanded by remember { mutableStateOf(false) }
    var selectedLabel by remember {
        mutableStateOf(optionLabels.getOrNull(options.indexOf(selectedAge)) ?: "No Limit")
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            shape = MaterialTheme.shapes.large,
            label = { Text("Maximum Age (Hours)") },
            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, value ->
                DropdownMenuItem(
                    text = { Text(optionLabels[index]) },
                    onClick = {
                        selectedLabel = optionLabels[index]
                        onAgeSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}