package com.example.booking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booking.ui.screens.InitialScreen
import com.example.booking.ui.screens.LoginScreen
import com.example.booking.ui.screens.MainScreen
import com.example.booking.ui.screens.RegisterScreen
import com.example.booking.ui.viewmodel.AuthUiState
import com.example.booking.ui.viewmodel.AuthViewModel

enum class BookingScreen {
    Initial,
    Login,
    Register,
    Main
}

@Composable
fun BookingApp(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            navController.navigate(BookingScreen.Main.name) {
                popUpTo(BookingScreen.Initial.name) { inclusive = true }
            }
            viewModel.resetState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = BookingScreen.Initial.name,
        modifier = modifier
    ) {
        composable(route = BookingScreen.Initial.name) {
            InitialScreen(
                onLoginClick = { navController.navigate(BookingScreen.Login.name) },
                onRegisterClick = { navController.navigate(BookingScreen.Register.name) }
            )
        }
        composable(route = BookingScreen.Login.name) {
            LoginScreen(
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                },
                onNavigateToRegister = {
                    navController.navigate(BookingScreen.Register.name) {
                        popUpTo(BookingScreen.Initial.name) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(route = BookingScreen.Register.name) {
            RegisterScreen(
                onRegisterClick = { request ->
                    viewModel.register(request)
                },
                onNavigateToLogin = {
                    navController.navigate(BookingScreen.Login.name) {
                        popUpTo(BookingScreen.Initial.name) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(route = BookingScreen.Main.name) {
            MainScreen(
                onLogout = {
                    navController.navigate(BookingScreen.Initial.name) {
                        popUpTo(BookingScreen.Main.name) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
