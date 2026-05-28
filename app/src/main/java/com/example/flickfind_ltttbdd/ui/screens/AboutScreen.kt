package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flickfind_ltttbdd.R

@Composable
fun AboutScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onNavigateToDeveloperInfo: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF172033) else Color(0xFFFFFFFF)
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // 1. LOGO
        Image(
            painter = painterResource(id = if (isDarkTheme) R.drawable.logo_v2 else R.drawable.logo_v4),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Tên logo (Flick Find)
        Text(
            text = "Flick Find",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Thanh giới thiệu nhà phát triển & Menu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (isDarkTheme) Color(0xFF162534) else Color(0xFFD1D9E6), RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onNavigateToDeveloperInfo() }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Giới thiệu nhà phát triển",
                    color = textColor,
                    fontSize = 16.sp
                )
            }
            
            IconButton(
                onClick = { /* Menu action */ },
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Bật tắt ánh sáng
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4A6592),
                    checkedTrackColor = Color(0xFF2C4162)
                )
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "Bật tắt ánh sáng",
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
