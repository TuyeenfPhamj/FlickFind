package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
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
        color = Color(0xFF0B121F) // Màu Navy đồng bộ toàn app
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF38B6FF)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMessage!!, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getMovieById(movieId) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38B6FF))
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
                movie != null -> {
                    MovieDetailContent(
                        movie = movie!!,
                        isFavorite = isFavorite,
                        onToggleFavorite = { viewModel.toggleFavorite(movie!!) },
                        onBackClick = onBackClick
                    )
                }
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: MovieResponse,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- PHẦN 1: HEADER (BACKDROP + NÚT BACK + NÚT YÊU THÍCH + POSTER) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp) // Chiều cao Header cố định
        ) {
            // 1.1 Ảnh Background (Backdrop) - Phủ kín toàn bộ Header để Poster không bị "rơi" ra ngoài
            AsyncImage(
                model = movie.backdropPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 1.2 Nút Back (Mũi tên) - Góc trái trên
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .statusBarsPadding()
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }

            // 1.3 Nút yêu thích (Trái tim) - Căn góc phải dưới của Backdrop
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 8.dp) // Nâng nhẹ nút lên khỏi mép
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF1E293B), CircleShape)
                    .border(1.dp, Color(0xFF38B6FF), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }

            // 1.4 Ảnh Poster - Thu nhỏ và nằm gọn trong ảnh nền
            Card(
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 12.dp) // Cách đáy 12dp để không bị "tràn" xuống nội dung
                    .width(100.dp)
                    .height(150.dp)
                    .align(Alignment.BottomStart),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                AsyncImage(
                    model = movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // --- PHẦN 2: KHUNG NỘI DUNG (Lên trên) ---
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF38B6FF), RoundedCornerShape(12.dp))
                .background(Color(0xFF131C2E).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nội dung:",
                    color = Color(0xFF38B6FF),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        // --- PHẦN 3: KHUNG THÔNG TIN CHI TIẾT (Xuống dưới) ---
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF38B6FF), RoundedCornerShape(12.dp))
                .background(Color(0xFF131C2E).copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Thông tin chi tiết",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF233044))
            
            DetailInfoRow("Thời lượng", "${movie.runtime} phút")
            DetailInfoRow("Đánh giá", "★ ${movie.rating}/10")
            DetailInfoRow("Thể loại", movie.genres.joinToString(", "))
            DetailInfoRow("Đạo diễn", movie.director)
            DetailInfoRow("Diễn viên", movie.cast)
            DetailInfoRow("Ngày ra mắt", movie.releaseDate)
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(
            text = value, 
            color = Color.White, 
            fontSize = 14.sp, 
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(max = 220.dp),
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

