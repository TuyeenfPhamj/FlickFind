package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onApplyFilters: (genre: String?, yearRange: String?) -> Unit
) {
    // [GHI CHÚ]: Đồng bộ màu sắc theo theme Sáng/Tối
    val backgroundColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF131C2E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val primaryColor = Color(0xFF38B6FF)

    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedYearRange by remember { mutableStateOf<String?>(null) }

    val genres = listOf(
        "Khoa học viễn tưởng", "Hành động", "Gây cấn", "Phiêu lưu", "Chính kịch",
        "Hình sự", "Hoạt hình", "Gia đình", "Giả tưởng", "Kinh dị",
        "Hài hước", "Tình cảm", "Chiến tranh", "Lịch sử", "Âm nhạc", "Bí ẩn"
    )
    val yearRanges = listOf("1991 - 1995", "1996 - 2000", "2001 - 2005", "2006 - 2010", "2011 - 2015", "2016 - 2020", "2021 - 2025")

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lọc phim", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Theo thể loại", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            genres.chunked(3).forEach { rowGenres ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowGenres.forEach { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = if (selectedGenre == genre) null else genre },
                            label = { 
                                Text(
                                    genre, 
                                    maxLines = 1, 
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontSize = 12.sp
                                ) 
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = cardColor,
                                labelColor = if (isDarkTheme) Color.Gray else Color.DarkGray,
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isDarkTheme) Color.Transparent else Color.LightGray,
                                enabled = true,
                                selected = selectedGenre == genre
                            )
                        )
                    }
                    repeat(3 - rowGenres.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Theo năm phát hành", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            yearRanges.chunked(2).forEach { rowYears ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowYears.forEach { range ->
                        FilterChip(
                            selected = selectedYearRange == range,
                            onClick = { selectedYearRange = if (selectedYearRange == range) null else range },
                            label = { Text(range, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = cardColor,
                                labelColor = if (isDarkTheme) Color.Gray else Color.DarkGray,
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isDarkTheme) Color.Transparent else Color.LightGray,
                                enabled = true,
                                selected = selectedYearRange == range
                            )
                        )
                    }
                    repeat(2 - rowYears.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onApplyFilters(selectedGenre, selectedYearRange) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Áp dụng", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
