package com.example.booking.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class MainScreenRoute(val route: String, val title: String, val icon: ImageVector) {
    object Home : MainScreenRoute("home", "Home", Icons.Default.Home)
    object Search : MainScreenRoute("search", "Search", Icons.Default.Search)
    object Reservations : MainScreenRoute("reservations", "Reservations", Icons.Default.DateRange)
    object Account : MainScreenRoute("account", "My Account", Icons.Default.Person)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    
    val items = listOf(
        MainScreenRoute.Home,
        MainScreenRoute.Search,
        MainScreenRoute.Reservations,
        MainScreenRoute.Account
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreenRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainScreenRoute.Home.route) {
                HomeScreen(
                    onReserveClick = { navController.navigate(MainScreenRoute.Search.route) },
                    onSeeReservationsClick = { navController.navigate(MainScreenRoute.Reservations.route) }
                )
            }
            composable(MainScreenRoute.Search.route) {
                SearchRoomScreen()
            }
            composable(MainScreenRoute.Reservations.route) {
                ReservationsScreen()
            }
            composable(MainScreenRoute.Account.route) {
                MyAccountScreen(onLogout = onLogout)
            }
        }
    }
}
