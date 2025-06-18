package ru.suhachev.weatherapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.presentation.screens.MainScreen
import ru.suhachev.weatherapp.presentation.screens.SearchScreen

@Composable
fun Navigation(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Main.route
    ) {
        composable(NavRoutes.Main.route) {
            MainScreen(
                onSearchClick = {
                    navController.navigate(NavRoutes.Search.route) {
                        launchSingleTop = true
                    }
                },
                viewModel = weatherViewModel
            )
        }
        
        composable(NavRoutes.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onCitySelected = { city ->
                    weatherViewModel.loadWeather(city)
                    navController.popBackStack()
                },
                viewModel = weatherViewModel
            )
        }
    }
} 