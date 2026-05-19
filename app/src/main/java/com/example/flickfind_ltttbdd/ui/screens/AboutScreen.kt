package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flickfind_ltttbdd.R

@Composable
fun AboutScreen() {
    val backgroundColor = Color(0xFF0D1724)
    val cardColor = Color(0xFF1B2A3E)
    val primaryColor = Color(0xFF155074)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Tiêu đề
        Text(
            text = "About & Help",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Start
        )

        // 2. Cụm Logo và Version (To và Sát khít nhau)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_v2),
                contentDescription = "App Logo",
                modifier = Modifier.size(350.dp).offset(y = (-25).dp),
                contentScale = ContentScale.Fit
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp)
            ) {
                Text(
                    text = "FlickFind App",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Version 1.0.0",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Thẻ Giới thiệu (Theo ảnh mẫu)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Giới thiệu",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dự án FlickFind được phát triển nhằm mang lại trải nghiệm tìm kiếm và quản lý phim tốt nhất cho người dùng.",
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Thẻ Đội ngũ phát triển (Theo ảnh mẫu - Dọc xuống)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Đội ngũ phát triển",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                val members = listOf(
                    "Phạm Văn Tuyền",
                    "Nguyễn Thế Lực",
                    "Ngô Bá Vĩnh",
                    "Nguyễn Thành Đạt"
                )
                
                members.forEachIndexed { index, name ->
                    Text(
                        text = "Thành viên ${index + 1}: $name",
                        color = Color.White,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Nút User Guide
        Button(
            onClick = { /* Xử lý sự kiện */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "User Guide (PDF)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
