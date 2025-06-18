package ru.suhachev.weatherapp.presentation.navigation

sealed class NavRoutes(val route: String) {
    data object Main : NavRoutes("main")
    data object Search : NavRoutes("search")
} 