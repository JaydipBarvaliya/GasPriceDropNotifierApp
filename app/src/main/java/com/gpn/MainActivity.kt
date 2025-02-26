package com.gpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.gpn.navigation.NavGraph
import com.gpn.viewmodel.GasPriceViewModel
import com.gpn.viewmodel.PriceAlertsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val gasPriceViewModel: GasPriceViewModel =
                    hiltViewModel() // ✅ Added missing ViewModel
                val gasAlertsViewModel: PriceAlertsViewModel = hiltViewModel()

                NavGraph(
                    gasPriceViewModel = gasPriceViewModel, // ✅ Passed to NavGraph
                    alertsModel = gasAlertsViewModel
                )
            }
        }
    }
}
