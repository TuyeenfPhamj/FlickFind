package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import com.example.flickfind_ltttbdd.navigation.bottomNavItems
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    movieId: String,
    viewModel: DetailViewModel,
    onBackClick: () -> Unit
) {
    val movie by viewModel.movie.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieById(movieId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F172A) // Màu nền xanh than đậm theo hình
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF0EA5E9)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMessage!!, color = Color.White)
                        Button(onClick = { viewModel.getMovieById(movieId) }) {
                            Text("Thử lại")
                        }
                    }
                }
                movie != null -> {
                    MovieDetailContent(
                        movie = movie!!,
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite(movie!!) }
                    )
                }
            }

            // Nút thoát (Góc trái trên - Hình chữ nhật)
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                border = borderStroke()
            ) {
                Text(
                    text = "Nút\nthoát",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: MovieResponse,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Ảnh Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Black)
        ) {
            AsyncImage(
                model = movie.backdropPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Text(
                text = "Ảnh Banner",
                color = Color.White,
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 2. Khung Thông tin phim (Có Border)
        Box(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF0EA5E9), RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    InfoText("Tên phim: ", movie.title)
                    InfoText("Thời gian: ", "${movie.runtime} phút")
                    InfoText("Thể loại: ", movie.genres.joinToString(", "))
                    InfoText("Đánh giá: ", "${movie.rating}/10")
                }

                // Nút thêm vào danh sách
                Button(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .width(120.dp)
                        .height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                    border = borderStroke()
                ) {
                    Text(
                        text = if (isFavorite) "Đã thêm\nvào list" else "Nút thêm\nvào danh\nsách",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 3. Khung Nội dung (Có Border)
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF0EA5E9), RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Text(
                text = "Nội dung:",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = movie.overview,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }

        // 4. Các nút dưới cùng (Giả lập theo hình)
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            containerColor = Color(0xFF1E293B)
        ) {
            bottomNavItems.forEach { screen ->
                NavigationBarItem(
                    selected = false,
                    onClick = { /* Giả lập điều hướng */ },
                    icon = {
                        Icon(
                            imageVector = screen.icon ?: Icons.Default.Home,
                            contentDescription = screen.title,
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF0EA5E9)
                    )
                )
            }
        }
    }
}

@Composable
fun InfoText(label: String, value: String) {
    Text(
        text = "$label $value",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

fun borderStroke() = BorderStroke(1.dp, Color(0xFF0EA5E9))

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val mockMovie = MovieResponse(
        id = 1,
        title = "Spider-Man",
        posterPath = "",
        backdropPath = "",
        genres = listOf("Hành động", "Phiêu lưu"),
        rating = 8.5f,
        runtime = 148,
        director = "Jon Watts",
        cast = "Tom Holland",
        releaseDate = "2021",
        overview = "Đây là nội dung mô tả của bộ phim mẫu để xem trước giao diện bố cục mới theo đúng hình ảnh yêu cầu."
    )
    
    Surface(color = Color(0xFF0F172A)) {
        MovieDetailContent(
            movie = mockMovie,
            isFavorite = false,
            onToggleFavorite = {}
        )
    }
}
