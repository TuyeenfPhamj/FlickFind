package com.example.flickfind_ltttbdd

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.flickfind_ltttbdd.navigation.MainNavGraph
import com.example.flickfind_ltttbdd.ui.theme.FlickFindLTTTBDDTheme
import android.view.WindowManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate")

        // 1. Kích hoạt Edge-to-Edge
        enableEdgeToEdge()

        // 2. Cho phép tràn vào vùng tai thỏ khi xoay ngang
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        setContent {
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemTheme) }
            FlickFindLTTTBDDTheme(darkTheme = isDarkTheme) {
                MainNavGraph(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
}