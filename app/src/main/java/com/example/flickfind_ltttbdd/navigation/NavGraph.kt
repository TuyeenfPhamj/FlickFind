package com.example.flickfind_ltttbdd.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.ProfileViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.AuthViewModel
import com.example.flickfind_ltttbdd.ui.screens.*

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider(context))
    val authUiState by authViewModel.uiState.collectAsState()

    // Chỉ hiển thị Bottom Bar ở 3 màn hình chính
    val rootScreens = listOf(
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )
    val showBottomBar = currentRoute in rootScreens || currentRoute == Screen.Register.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 0. Màn hình Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // 1. Màn hình Trang chủ (Công khai)
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider(context))
                HomeScreen(viewModel = homeViewModel, navController = navController)
            }

            // 2. Màn hình Cá nhân (Yêu cầu đăng nhập)
            composable(Screen.Profile.route) {
                if (authUiState.isLoggedIn) {
                    // Nếu đã đăng nhập -> Hiện trang cá nhân
                    val profileViewModel: ProfileViewModel = viewModel(
                        factory = AppViewModelProvider(context)
                    )
                    ProfileScreen(
                        viewModel = profileViewModel,
                        navController = navController,
                        onLogout = {
                            authViewModel.logout()
                        }
                    )
                } else {
                    // Nếu chưa đăng nhập -> Hiện trang đăng nhập ngay tại tab Cá nhân
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                    )
                }
            }

            // 3. Màn hình Cài đặt (Công khai)
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            // 4. Màn hình Chi tiết phim
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                MovieDetailScreen(movieId = movieId, navController = navController)
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Text("Màn hình Cài đặt - Đang xây dựng")
}

@Composable
fun MovieDetailScreen(movieId: Int, navController: NavHostController) {
    Text("Chi tiết phim ID: $movieId")
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navigationItems = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
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
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}