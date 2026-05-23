package com.example.flickfind_ltttbdd.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

// Định nghĩa các màn hình trong ứng dụng
sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {

    // 3 Màn hình chính xuất hiện trên Bottom Navigation Bar
    object Home : Screen("home", "Khám phá", Icons.Default.Home)
    object Profile : Screen("profile", "Cá nhân", Icons.Default.Person)
    object Settings : Screen("settings", "Cài đặt", Icons.Default.Settings)

    // Màn hình phụ không xuất hiện trên Bottom Bar (nên không cần truyền Icon)
    object DeveloperInfo : Screen("developer_info", "Thông tin nhà phát triển")
    object Detail : Screen("detail/{movieId}", "Chi tiết") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}

// Danh sách các mục sẽ xuất hiện dưới Bottom Navigation Bar
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Profile,
    Screen.Settings
)