package com.example.flickfind_ltttbdd.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.ProfileViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel
import com.example.flickfind_ltttbdd.ui.screens.ProfileScreen
import com.example.flickfind_ltttbdd.ui.screens.HomeScreen
import com.example.flickfind_ltttbdd.ui.screens.AboutScreen
import com.example.flickfind_ltttbdd.ui.screens.DetailScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Màn hình 1: Trang chủ
            composable(Screen.Home.route) {
                val context = LocalContext.current
                val homeViewModel: HomeViewModel = viewModel(
                    factory = AppViewModelProvider(context)
                )
                HomeScreen(viewModel = homeViewModel, navController = navController)
            }

            // Màn hình 2: Cá nhân
            composable(Screen.Profile.route) {
                val context = LocalContext.current
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = AppViewModelProvider(context)
                )
                ProfileScreen(viewModel = profileViewModel, navController = navController)
            }

            // Màn hình 3: Giới thiệu
            composable(Screen.About.route) {
                AboutScreen()
            }

            // Màn hình 4: Chi tiết phim
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    androidx.navigation.navArgument("movieId") { type = androidx.navigation.NavType.IntType }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                val context = LocalContext.current
                val detailViewModel: DetailViewModel = viewModel(
                    factory = AppViewModelProvider(context, movieId)
                )
                DetailScreen(viewModel = detailViewModel, navController = navController)
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navigationItems = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.About
    )
    val appSurface = Color(0xFF171E30)
    val appIndicator = Color(0xFF1A2844)
    val textActive = Color(0xFFFFFFFF)
    val textInactive = Color(0xFF8E9AA6)
    
    NavigationBar(
        containerColor = appSurface,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationItems.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (isSelected) textActive else textInactive
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (isSelected) textActive else textInactive
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = appIndicator
                )
            )
        }
    }
}
