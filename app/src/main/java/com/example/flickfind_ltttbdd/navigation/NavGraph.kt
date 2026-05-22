package com.example.flickfind_ltttbdd.navigation

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.ProfileViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel

import com.example.flickfind_ltttbdd.ui.viewmodel.SearchViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.AuthViewModel
import com.example.flickfind_ltttbdd.ui.screens.*

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider(context))
    val authState by authViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { 
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            // Hiện Bottom Bar ở các màn chính, ẩn ở Login/Register nếu cần, hoặc hiện tất cả
            val hideBottomBarRoutes = listOf(Screen.Login.route, Screen.Register.route)
            if (currentRoute !in hideBottomBarRoutes) {
                AppBottomNavigationBar(navController) 
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Màn hình 1: Trang chủ
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider(context))
                HomeScreen(viewModel = homeViewModel, navController = navController)
            }

            // Màn hình 2: Cá nhân (Kiểm tra đăng nhập ở đây)
            composable(Screen.Profile.route) {
                if (authState.isLoggedIn) {
                    val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider(context))
                    ProfileScreen(
                        viewModel = profileViewModel, 
                        navController = navController, 
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0)
                            }
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onLoginSuccess = {
                            // Tự động chuyển sang Profile khi đăng nhập xong nhờ Recomposition
                        }
                    )
                }
            }

            // Màn hình Đăng ký
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
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
                val detailViewModel: DetailViewModel = viewModel(factory = AppViewModelProvider(context, movieId))
                DetailScreen(viewModel = detailViewModel, navController = navController)
            }

            // Màn hình 5: Lọc phim
            composable(Screen.Filter.route) {
                FilterScreen(
                    navController = navController,
                    onApplyFilters = { genre, yearRange ->
                        navController.navigate(Screen.SearchResult.createRoute(genre = genre, yearRange = yearRange))
                    }
                )
            }

            // Màn hình 6: Kết quả tìm kiếm
            composable(
                route = Screen.SearchResult.route,
                arguments = Screen.SearchResult.arguments
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query")
                val genre = backStackEntry.arguments?.getString("genre")
                val yearRange = backStackEntry.arguments?.getString("yearRange")
                val searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider(context))
                SearchResultScreen(
                    query = query, genre = genre, yearRange = yearRange,
                    viewModel = searchViewModel, navController = navController
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navigationItems = listOf(Screen.Home, Screen.Profile, Screen.About)
    val appSurface = Color(0xFF171E30)
    val appIndicator = Color(0xFF1A2844)
    val textActive = Color(0xFFFFFFFF)
    val textInactive = Color(0xFF8E9AA6)
    
    NavigationBar(containerColor = appSurface, tonalElevation = 0.dp) {
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
                label = { Text(text = screen.title, color = if (isSelected) textActive else textInactive) },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (isSelected) textActive else textInactive
                    )
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = appIndicator)
            )
        }
    }
}
