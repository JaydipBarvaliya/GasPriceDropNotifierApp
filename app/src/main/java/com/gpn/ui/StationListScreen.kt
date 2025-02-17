package com.gpn.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpn.data.GasStation

@Composable
fun StationListScreen(viewModel: GasPriceViewModel) {
    val stations = viewModel.stationsState.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(stations) { station ->
            StationCard(station)
        }
    }
}

@Composable
fun StationCard(station: GasStation) {
    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = station.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "${station.address.line1}, ${station.address.locality}")
            // Display prices if needed
            station.prices?.forEach { price ->
                Text("Price: ${price.credit?.formattedPrice ?: "N/A"}")
            }
        }
    }
}
