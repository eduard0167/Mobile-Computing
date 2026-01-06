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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booking.R

sealed class MainScreenRoute(val route: String, val titleResId: Int, val icon: ImageVector) {
    object Home : MainScreenRoute("home", R.string.home_tab, Icons.Default.Home)
    object Search : MainScreenRoute("search", R.string.search_tab, Icons.Default.Search)
    object Reservations :
        MainScreenRoute("reservations", R.string.reservations_tab, Icons.Default.DateRange)

    object Account : MainScreenRoute("account", R.string.account_tab, Icons.Default.Person)
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
                    val title = stringResource(id = screen.titleResId)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = title) },
                        label = { Text(title) },
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
                SearchBuildingScreen(
                    onBuildingSelected = { buildingId ->
                        navController.navigate("search_room/$buildingId")
                    }
                )
            }

            composable(
                route = "search_room/{buildingId}",
                arguments = listOf(navArgument("buildingId") { type = NavType.IntType })
            ) { backStackEntry ->
                val buildingId = backStackEntry.arguments?.getInt("buildingId") ?: 0
                SearchRoomScreen(
                    buildingId = buildingId,
                    onRoomSelected = { roomId ->
                        println("Selected room ID: $roomId")
                    }
                )
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
