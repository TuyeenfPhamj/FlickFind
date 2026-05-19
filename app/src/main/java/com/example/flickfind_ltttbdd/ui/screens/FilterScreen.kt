package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    onApplyFilters: (genre: String?, yearRange: String?) -> Unit
) {
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedYearRange by remember { mutableStateOf<String?>(null) }

    val genres = listOf("Hành động", "Kinh dị", "Anime", "Hài hước", "Viễn tưởng", "Tâm lý")
    val yearRanges = listOf("1991 - 1995", "1996 - 2000", "2001 - 2005", "2006 - 2010", "2011 - 2015", "2016 - 2020", "2021 - 2025")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lọc phim", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B121F))
            )
        },
        containerColor = Color(0xFF0B121F)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Theo thể loại", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid thể loại
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genres) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { selectedGenre = if (selectedGenre == genre) null else genre },
                        label = { Text(genre) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF131C2E),
                            labelColor = Color.Gray,
                            selectedContainerColor = Color(0xFF38B6FF),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Theo năm phát hành", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Grid năm
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(yearRanges) { range ->
                    FilterChip(
                        selected = selectedYearRange == range,
                        onClick = { selectedYearRange = if (selectedYearRange == range) null else range },
                        label = { Text(range, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF131C2E),
                            labelColor = Color.Gray,
                            selectedContainerColor = Color(0xFF38B6FF),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Button(
                onClick = { onApplyFilters(selectedGenre, selectedYearRange) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Áp dụng", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
