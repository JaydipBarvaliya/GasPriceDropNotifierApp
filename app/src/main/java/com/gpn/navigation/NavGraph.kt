package com.gpn.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gpn.ui.PriceAlertsScreen
import com.gpn.ui.StationListScreen
import com.gpn.viewmodel.GasPriceViewModel
import com.gpn.viewmodel.PriceAlertsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavGraph(gasPriceViewModel: GasPriceViewModel, alertsModel: PriceAlertsViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, drawerState, scope)
        }
    ) {
        NavHost(navController = navController, startDestination = "search") {
            composable("search") { SearchScreen(gasPriceViewModel) } // ✅ Pass ViewModel here
            composable("alerts") { PriceAlertsScreen(alertsModel) }
        }
    }
}

@Composable
fun DrawerContent(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    ModalDrawerSheet {
        Text("Menu", modifier = Modifier.padding(16.dp))
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Find Gas Stations") },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("search")
            }
        )
        NavigationDrawerItem(
            label = { Text("Alerts") },
            selected = false,
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("alerts")
            }
        )
    }
}

@Composable
fun SearchScreen(viewModel: GasPriceViewModel) {
    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            StationListScreen(viewModel = viewModel)
        }
    }
}

