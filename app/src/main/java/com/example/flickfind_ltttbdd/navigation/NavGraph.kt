package com.example.flickfind_ltttbdd.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.SearchViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel
import com.example.flickfind_ltttbdd.ui.screens.HomeScreen
import com.example.flickfind_ltttbdd.ui.screens.FilterScreen
import com.example.flickfind_ltttbdd.ui.screens.SearchResultScreen
import com.example.flickfind_ltttbdd.ui.screens.DetailScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Chỉ hiển thị Bottom Bar ở 3 màn hình chính
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Profile.route,
        Screen.About.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Detail.createRoute(1), // Chạy thẳng vào Chi tiết để test
            modifier = Modifier.padding(if (showBottomBar) innerPadding else PaddingValues(0.dp))
        ) {
            composable(Screen.Home.route) {
                val context = LocalContext.current
                val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider(context))
                HomeScreen(viewModel = homeViewModel, navController = navController)
            }

            composable(Screen.Filter.route) {
                FilterScreen(
                    navController = navController,
                    onApplyFilters = { genre, yearRange ->
                        navController.navigate(Screen.SearchResult.createRoute(genre = genre, yearRange = yearRange))
                    }
                )
            }

            composable(
                route = Screen.SearchResult.route,
                arguments = Screen.SearchResult.arguments
            ) { backStackEntry ->
                val context = LocalContext.current
                val searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider(context))
                val query = backStackEntry.arguments?.getString("query")
                val genre = backStackEntry.arguments?.getString("genre")
                val yearRange = backStackEntry.arguments?.getString("yearRange")
                
                SearchResultScreen(
                    query = query,
                    genre = genre,
                    yearRange = yearRange,
                    viewModel = searchViewModel,
                    navController = navController
                )
            }

            composable(Screen.Profile.route) { Text("Màn hình Cá nhân", color = Color.White) }
            composable(Screen.About.route) { Text("Màn hình Giới thiệu", color = Color.White) }
            
            // Màn hình Chi tiết
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: "1"
                val context = LocalContext.current
                val detailViewModel: DetailViewModel = viewModel(factory = AppViewModelProvider(context))
                
                DetailScreen(
                    movieId = movieId,
                    viewModel = detailViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navigationItems = listOf(Screen.Home, Screen.Profile, Screen.About)
    NavigationBar(containerColor = Color(0xFF171E30)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(text = screen.title, color = if (isSelected) Color.White else Color.Gray) },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (isSelected) Color.White else Color.Gray
                    )
                }
            )
        }
    }
}
