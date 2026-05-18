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
import com.example.flickfind_ltttbdd.ui.screens.DetailScreen
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel
import com.example.flickfind_ltttbdd.ui.screens.HomeScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Chỉ hiện Bottom Bar ở các màn hình chính
    val showBottomBar = currentRoute in listOf(Screen.Home.route, Screen.Profile.route, Screen.About.route)

    Scaffold(
        bottomBar = { 
            if (showBottomBar) {
                AppBottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->

        // Khung NavHost liên kết các màn hình theo cấu trúc của bạn
        NavHost(
            navController = navController,
            startDestination = Screen.Detail.createRoute(1), // Sử dụng hàm tạo route mẫu
            modifier = Modifier.padding(if (showBottomBar) innerPadding else PaddingValues(0.dp))
        ) {
            // Màn hình 1: Khám phá (Trang chủ)
            composable(Screen.Home.route) {
                val context = LocalContext.current
                val homeViewModel: HomeViewModel = viewModel(
                    factory = AppViewModelProvider(context)
                )
                HomeScreen(viewModel = homeViewModel, navController = navController)
            }

            // Màn hình Chi tiết
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("movieId") { type = NavType.StringType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                val context = LocalContext.current
                val detailViewModel: DetailViewModel = viewModel(
                    factory = AppViewModelProvider(context)
                )
                DetailScreen(
                    movieId = movieId,
                    viewModel = detailViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

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