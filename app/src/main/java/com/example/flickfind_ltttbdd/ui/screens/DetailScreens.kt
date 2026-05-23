package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.ui.viewmodel.AppViewModelProvider
import com.example.flickfind_ltttbdd.ui.viewmodel.DetailViewModel

@Composable
fun DetailScreen(
    movieId: Int?,
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider(LocalContext.current))
) {
    val movie by viewModel.movie.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    // Tự động tải dữ liệu khi vào màn hình
    LaunchedEffect(movieId) {
        if (movieId != null) {
            viewModel.getMovieById(movieId)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B121F) // Màu Navy đồng bộ với Home
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF38B6FF)
                )
            } else if (error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error ?: "Đã xảy ra lỗi", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { movieId?.let { viewModel.getMovieById(it) } }) {
                        Text("Thử lại")
                    }
                }
            } else {
                movie?.let { movieData ->
                    MovieDetailContent(
                        movieData = movieData,
                        isFavorite = isFavorite,
                        onBackClick = onBackClick,
                        onToggleFavorite = { viewModel.toggleFavorite(movieData) }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movieData: com.example.flickfind_ltttbdd.data.remote.MovieResponse,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- PHẦN 1: HEADER (BACKDROP + POSTER + BACK + FAVORITE) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            // 1.1 Ảnh Background (Backdrop)
            AsyncImage(
                model = movieData.backdropPath,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )

            // Hiệu ứng mờ (Gradient) chuyển tiếp xuống nội dung
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF0B121F)),
                            startY = 400f
                        )
                    )
            )

            // 1.2 Nút Quay lại (Góc trên trái)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(12.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // 1.3 Ảnh Poster (Đè lên ảnh nền, lệch trái)
            AsyncImage(
                model = movieData.posterPath,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .width(130.dp)
                    .height(190.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )

            // 1.4 Nút Yêu thích (Hình trái tim - Góc dưới phải Header)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .padding(end = 24.dp, bottom = 10.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // --- PHẦN 2: THÔNG TIN CHI TIẾT ---
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = movieData.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Khung Nội dung
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111C2F), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nội dung",
                    color = Color(0xFF38B6FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movieData.overview,
                    color = Color.LightGray,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Khung Chi tiết
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF111C2F), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Thông tin phim",
                    color = Color(0xFF38B6FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                DetailItemRow("⭐ Đánh giá", "${movieData.rating}/10")
                DetailItemRow("🕒 Thời lượng", "${movieData.runtime} phút")
                DetailItemRow("🎭 Thể loại", movieData.genres.joinToString(", "))
                DetailItemRow("🎬 Đạo diễn", movieData.director)
                DetailItemRow("👥 Diễn viên", movieData.cast)
                DetailItemRow("📅 Ngày ra mắt", movieData.releaseDate)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label: ",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}


