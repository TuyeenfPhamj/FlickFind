package com.example.flickfind_ltttbdd.navigation


import android.net.http.SslCertificate.saveState
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
//import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
//import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
//import com.example.flickfind_ltttbdd.ui.screens.HomeScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController) }
    ) { innerPadding ->

        // Khung NavHost liên kết các màn hình theo cấu trúc của bạn
        NavHost(navController = navController,
            startDestination = Screen.Home.route,
            // Thay vì padding toàn bộ, hãy chỉ padding bottom để không đè lên BottomBar
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        )  {
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
    val navigationItems = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.About
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Lấy trạng thái màn hình hiện tại để check làm sáng nút
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        navigationItems.forEach { screen ->
            // ĐIỀU KIỆN LÀM SÁNG: Nếu route trùng khớp thì mục đó sẽ sáng lên
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