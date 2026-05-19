package com.example.flickfind_ltttbdd.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Home : Screen("home", "Khám phá", Icons.Default.Home)
    object Profile : Screen("profile", "Cá nhân", Icons.Default.Person)
    object About : Screen("about", "Giới thiệu", Icons.Default.Info)
}
