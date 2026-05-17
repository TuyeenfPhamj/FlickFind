package com.example.flickfind_ltttbdd.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.screens.HomeScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController) }
    ) { innerPadding ->

        // Khung NavHost liên kết các màn hình theo cấu trúc của bạn
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Màn hình 1: Khám phá (Trang chủ của bạn)
//            composable(Screen.Home.route) {
//                val context = LocalContext.current
//                val homeViewModel: HomeViewModel = viewModel(
//                    factory = AppViewModelProvider(context)
//                )
//                HomeScreen(viewModel = homeViewModel, navController = navController)
//            }

            // Màn hình 2: Cá nhân (Giao diện phụ trách của thành viên khác)
            composable(Screen.Profile.route) {
                Text("Màn hình Cá nhân - Đang xây dựng")
            }

            // Màn hình 3: Giới thiệu (Giao diện phụ trách của thành viên khác)
            composable(Screen.About.route) {
                Text("Màn hình Giới thiệu - Đang xây dựng")
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(imageVector = it, contentDescription = screen.title)
                    }
                },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}