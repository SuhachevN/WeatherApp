package ru.suhachev.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.suhachev.weatherapp.presentation.viewmodel.WeatherViewModel
import ru.suhachev.weatherapp.screens.MainScreen
import ru.suhachev.weatherapp.screens.SearchScreen

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Search : Screen("search")
}

@Composable
fun Navigation(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val viewModel: WeatherViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                viewModel = viewModel
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onCitySelected = { city ->
                    viewModel.loadWeather(city)
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
} 