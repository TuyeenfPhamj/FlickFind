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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
    isDarkTheme: Boolean,
    onBackClick: () -> Unit
) {
    // [GHI CHÚ]: Đồng bộ màu sắc theo theme Sáng/Tối
    val backgroundColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF131C2E) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val primaryColor = Color(0xFF38B6FF)

    val movie by viewModel.movie.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieById(movieId)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryColor
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error!!, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getMovieById(movieId) },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Text("Thử lại", color = Color.White)
                        }
                    }
                }
                movie != null -> {
                    MovieDetailContent(
                        movie = movie!!,
                        isFavorite = isFavorite,
                        textColor = textColor,
                        cardColor = cardColor,
                        primaryColor = primaryColor,
                        isDarkTheme = isDarkTheme,
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
    textColor: Color,
    cardColor: Color,
    primaryColor: Color,
    isDarkTheme: Boolean,
    onToggleFavorite: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            AsyncImage(
                model = movie.backdropPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

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

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 8.dp)
                    .align(Alignment.BottomEnd)
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color.White, CircleShape)
                    .border(1.dp, primaryColor, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) Color.Red else (if (isDarkTheme) Color.White else Color.Black)
                )
            }

            Card(
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 12.dp)
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

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .border(1.dp, primaryColor, RoundedCornerShape(12.dp))
                .background(cardColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = movie.title,
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nội dung:",
                    color = primaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview,
                    color = if (isDarkTheme) Color.LightGray else Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth()
                .border(1.dp, primaryColor, RoundedCornerShape(12.dp))
                .background(cardColor.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Thông tin chi tiết",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = primaryColor.copy(alpha = 0.3f))

            DetailInfoRow("Thời lượng", "${movie.runtime} phút", textColor)
            DetailInfoRow("Đánh giá", "★ ${movie.rating}/10", textColor)
            DetailInfoRow("Thể loại", movie.genres.joinToString(", "), textColor)
            DetailInfoRow("Đạo diễn", movie.director, textColor)
            DetailInfoRow("Diễn viên", movie.cast, textColor)
            DetailInfoRow("Ngày ra mắt", movie.releaseDate, textColor)
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(
            text = value,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.widthIn(max = 220.dp),
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
