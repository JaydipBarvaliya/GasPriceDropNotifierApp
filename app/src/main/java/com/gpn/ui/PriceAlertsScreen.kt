package com.gpn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpn.network.Alert
import com.gpn.viewmodel.PriceAlertsViewModel

@Composable
fun PriceAlertsScreen(viewModel: PriceAlertsViewModel) {
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
                Text(text = "No alerts found", modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(alerts) { alert ->
                        AlertItem(alert)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Station Name
            Text(
                text = alert.name ?: "...",
                style = MaterialTheme.typography.titleLarge
            )

            // Address
            Text(
                text = alert.formattedAddress().ifEmpty { "..." },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expected Price
            Text(
                text = "Price Drop Below: ${alert.expectedPrice}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Fuel Type
            Text(
                text = "Fuel Type: Regular", // Assuming 1 = Regular (Modify as per API mapping)
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { /* Handle modify action */ }) {
                    Text("Modify Alert")
                }

                Button(
                    onClick = { /* Handle delete action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Alert")
                }
            }
        }
    }
}
