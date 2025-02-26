package com.gpn.ui

import GasStation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpn.viewmodel.GasPriceViewModel
import kotlinx.coroutines.launch

@Composable
fun StationListScreen(viewModel: GasPriceViewModel) {
    val stations by viewModel.stationsState.collectAsStateWithLifecycle()
    val error by viewModel.errorState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔍 Search Bar
        item {
            SearchScreen(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ⚠️ Error or Empty State Handling
        item {
            when {
                error != null -> ErrorMessage(error!!)
                stations.isEmpty() -> EmptyState()
            }
        }

        // ⛽ List of Gas Stations
        items(stations) { station ->
            GasStationCard(station, viewModel)
            HorizontalDivider()
        }
    }

    // 📢 Alert Dialog (if active)
    CreateAlertDialog(viewModel, remember { SnackbarHostState() })
}

@Composable
private fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Text(
            text = "Error: $error",
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Info, // Changed to outlined style for a modern look
            contentDescription = "No Stations",
            modifier = Modifier.size(72.dp), // Slightly larger for better visibility
            tint = MaterialTheme.colorScheme.secondary // Softer highlight color
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "No Stations Found",
            style = MaterialTheme.typography.headlineSmall, // Larger and more readable
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // Softer color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Try adjusting your search filters and try again!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun GasStationCard(station: GasStation, viewModel: GasPriceViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 🏪 Gas Station Name & Address
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = station.address.line1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ⛽ Fuel Prices
            val fuelPrices = mapOf(
                "Regular" to station.prices.getOrNull(0)?.credit?.formattedPrice,
                "Midgrade" to station.prices.getOrNull(1)?.credit?.formattedPrice,
                "Premium" to station.prices.getOrNull(2)?.credit?.formattedPrice,
                "Diesel" to station.prices.getOrNull(3)?.credit?.formattedPrice
            )

            Column {
                fuelPrices.forEach { (fuelType, price) ->
                    Text(
                        text = "$fuelType: ${price ?: "No Price Found"}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (price == null) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔔 Create Alert Button
            Button(
                onClick = { viewModel.showCreateAlertDialog(station) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Alert", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAlertDialog(viewModel: GasPriceViewModel, snackbarHostState: SnackbarHostState) {
    val selectedStation by viewModel.selectedStation.collectAsState()
    val selectedFuelType by viewModel.selectedFuelType
    val expectedPrice by viewModel.expectedPrice

    if (viewModel.isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog() },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fuel Type Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    val fuelTypes = listOf("Regular", "Midgrade", "Premium", "Diesel", "E85", "UNL88")

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedFuelType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fuel Type") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            fuelTypes.forEach { fuel ->
                                DropdownMenuItem(
                                    text = { Text(fuel) },
                                    onClick = {
                                        viewModel.selectedFuelType.value = fuel
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Expected Price Input
                    TextField(
                        value = expectedPrice.toString(),
                        onValueChange = { input ->
                            viewModel.expectedPrice.floatValue = input.toFloatOrNull() ?: 0.0f // Convert String to Float safely
                        },
                        label = { Text("Expected Price") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    val coroutineScope = rememberCoroutineScope()
                    // Create Alert Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.createPriceAlert()
                                snackbarHostState.showSnackbar("Alert Created Successfully!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Create Alert", color = Color.White)
                    }


                    // Close Button
                    Button(
                        onClick = { viewModel.closeDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            },
            title = { Text("Create Alert") },
            text = {
                Text("Set an alert for ${selectedStation?.name ?: "this station"}?")
            }
        )
    }
}


