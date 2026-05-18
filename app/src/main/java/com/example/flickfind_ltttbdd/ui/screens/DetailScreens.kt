package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.remote.MovieResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movieId: Int?,
    onBackClick: () -> Unit
) {
    // Giả sử bạn lấy được thông tin movie từ ViewModel dựa trên movieId
    // Đây là giao diện mẫu
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết phim") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Xử lý yêu thích */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Backdrop Image
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500/backdrop_path_sample", // Thay bằng movie.backdropPath
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tên phim mẫu", // movie.title
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(text = "⭐ 8.5", color = MaterialTheme.colorScheme.primary) // movie.rating
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "120 phút") // movie.runtime
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Thể loại: Hành động, Phiêu lưu", // movie.genres.joinToString()
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nội dung",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Đây là phần tóm tắt nội dung phim...", // movie.overview
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Đạo diễn: Christopher Nolan", // movie.director
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Diễn viên: Leonardo DiCaprio, Joseph Gordon-Levitt", // movie.cast
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}