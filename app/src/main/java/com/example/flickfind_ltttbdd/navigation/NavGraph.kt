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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flickfind_ltttbdd.ui.screens.AboutScreen

@Composable

fun MainNavGraph() {
    val navController = rememberNavController()

    Scaffold(

        bottomBar = { AppBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.About.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Màn hình Trang chủ - Đang phát triển")
                }
            }

            composable(Screen.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Màn hình Cá nhân - Đang phát triển")
                }
            }

            composable(Screen.About.route) {
                AboutScreen()
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
    val AppSurface = Color(0xFF171E30)    // Màu thanh điều hướng (trùng màu ô tìm kiếm của bạn)
    val AppIndicator = Color(0xFF1A2844)  // Màu vòng bo (viên nhộng) bọc Icon khi được chọn
    val TextActive = Color(0xFFFFFFFF)    // Chữ và Icon sáng trắng khi Active
    val TextInactive = Color(0xFF8E9AA6)
    NavigationBar(
        containerColor = AppSurface,
        tonalElevation = 0.dp
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
                        color = if (isSelected) TextActive else TextInactive
                    )
                },
                icon = {
                    Icon(
                        // Sửa lỗi ImageVector? bằng cách thêm toán tử dự phòng ?:
                        imageVector = screen.icon ?: Icons.Default.Home,
                        contentDescription = screen.title,
                        tint = if (isSelected) TextActive else TextInactive
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = AppIndicator
                )
            )
        }
    }
}