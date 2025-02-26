package com.gpn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpn.network.Alert
import com.gpn.viewmodel.PriceAlertsViewModel

@Composable
fun PriceAlertsScreen(viewModel: PriceAlertsViewModel) {

    var showDeleteAllDialog by remember { mutableStateOf(false) }
    val alerts by viewModel.alerts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAlerts()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (alerts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,  // You can change this icon
                        contentDescription = "No Alerts",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "No alerts found",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Add an alert to track price drops!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn( modifier = Modifier.weight(1f),
                     verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(alerts) { alert ->
                        AlertItem(viewModel, alert)
                    }
                }

                if (alerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 🚨 Delete All Alerts Button
                    Button(
                        onClick = { showDeleteAllDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete All Alerts")
                    }
                }
            }
        }
    }

    // 🔴 Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete all alerts? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllAlerts()
                        showDeleteAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Composable
fun AlertItem(viewModel: PriceAlertsViewModel, alert: Alert) {
    var showModifyDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) } // Add state for delete confirmation

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = alert.name ?: "...", style = MaterialTheme.typography.titleLarge)
            Text(text = alert.formattedAddress().ifEmpty { "..." }, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price Drop Below: ${alert.expectedPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Fuel Type: ${alert.fuelType}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { showModifyDialog = true }) {
                    Text("Modify Alert")
                }

                Button(
                    onClick = { showDeleteDialog = true }, // Show confirmation dialog
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Alert")
                }
            }
        }
    }

    // Show Modify Dialog
    if (showModifyDialog) {
        ModifyAlertDialog(
            alert = alert,
            onDismiss = { showModifyDialog = false },
            onUpdate = { updatedAlert ->
                viewModel.updateAlert(updatedAlert)
                showModifyDialog = false
            },
            onDelete = {
                showDeleteDialog = true // Show delete confirmation
                showModifyDialog = false
            }
        )
    }

    // Show Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Alert") },
            text = { Text("Are you sure you want to delete this alert?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAlert(alert.id) // Perform delete action
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
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


