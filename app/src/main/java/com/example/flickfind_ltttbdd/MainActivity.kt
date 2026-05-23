package com.example.flickfind_ltttbdd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickfind_ltttbdd.navigation.MainNavGraph
import com.example.flickfind_ltttbdd.ui.theme.FlickFindLTTTBDDTheme
import android.view.WindowManager
import androidx.core.view.WindowCompat
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Kích hoạt Edge-to-Edge
        enableEdgeToEdge()

        // 2. Cho phép tràn vào vùng tai thỏ khi xoay ngang
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        setContent {
            FlickFindLTTTBDDTheme {
                MainNavGraph()
            }
        }
    }
}