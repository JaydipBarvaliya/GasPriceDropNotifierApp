package com.gpn.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.gpn.MainActivity
import com.gpn.ui.LoginScreen
import com.gpn.ui.PriceAlertsScreen
import com.gpn.ui.StationListScreen
import com.gpn.viewmodel.GasPriceViewModel
import com.gpn.viewmodel.PriceAlertsViewModel

@Composable
fun NavGraph(gasPriceViewModel: GasPriceViewModel, alertsModel: PriceAlertsViewModel) {

    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        bottomBar = {
            if (currentUser != null) BottomNavBar(navController) } // Hide navbar on LoginScreen
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (currentUser == null) "login" else "search",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(onGoogleSignIn = { signInWithGoogle(navController) }) }
            composable("search") { StationListScreen(viewModel = gasPriceViewModel) } // ✅ Updated Navigation
            composable("alerts") { PriceAlertsScreen(alertsModel) }
        }
    }
}
fun signInWithGoogle(navController: NavController) {
    val activity = navController.context as? ComponentActivity ?: return
    (activity as? MainActivity)?.signInWithGoogle()
}


@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("search", "Find Gas", Icons.Default.Search),
        BottomNavItem("alerts", "Alerts", Icons.Default.Notifications)
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)