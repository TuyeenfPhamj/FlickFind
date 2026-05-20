package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val bgColor = Color(0xFF0B101B)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết phim", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF172033))
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.errorMessage ?: "Lỗi không xác định", color = Color.Red)
            }
        } else {
            uiState.movie?.let { movie ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Backdrop Image
                    AsyncImage(
                        model = if (movie.backdropPath.startsWith("/")) "https://image.tmdb.org/t/p/w500${movie.backdropPath}" else movie.backdropPath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = movie.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = movie.rating.toString(), color = Color.White, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${movie.runtime} phút", color = Color.Gray, fontSize = 16.sp)
                        }

                        Text(
                            text = "Thể loại: ${movie.genres.joinToString(", ")}",
                            color = Color.Cyan,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Nội dung",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.overview,
                            color = Color.LightGray,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Thông tin thêm
                        InfoRow(label = "Đạo diễn", value = movie.director)
                        InfoRow(label = "Diễn viên", value = movie.cast)
                        InfoRow(label = "Ngày phát hành", value = movie.releaseDate)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 14.sp)
    }
}
