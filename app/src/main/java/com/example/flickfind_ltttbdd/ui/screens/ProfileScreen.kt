package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.ui.viewmodel.ProfileViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickfind_ltttbdd.data.local.UserEntity

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val bgColor = Color(0xFF0B101B)
    var isEditMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            ProfileHeader(
                name = uiState.user?.name ?: "Người dùng",
                avatarUrl = uiState.user?.avatarUrl ?: "",
                onLogout = onLogout
            )
        }

        // 2. Statistics
        item {
            GenreDistributionSection(
                genreDistribution = uiState.genreDistribution,
                totalTime = uiState.totalWatchTime
            )
        }

        // 3. Title Section with Edit Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách yêu thích",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    onClick = { isEditMode = !isEditMode },
                    color = if (isEditMode) Color(0xFFE91E63) else Color(0xFF172033),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEditMode) "Xong" else "Sửa",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 4. Horizontal Favorite Movie List
        items(uiState.favoriteMovies) { movie ->
            HorizontalFavoriteMovieItem(
                movie = movie,
                isEditMode = isEditMode,
                onToggleWatched = { viewModel.toggleWatched(movie) },
                onDelete = { viewModel.deleteFavorite(movie) }
            )
        }
    }
}

@Composable
fun HorizontalFavoriteMovieItem(
    movie: FavoriteMovieEntity,
    isEditMode: Boolean,
    onToggleWatched: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh phim bên trái
            Box(modifier = Modifier.size(width = 80.dp, height = 110.dp)) {
                val imageUrl = if (movie.posterPath.startsWith("/")) {
                    "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                } else {
                    movie.posterPath
                }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Nhãn trạng thái nhỏ trên ảnh
                Surface(
                    color = if (movie.isWatched) Color(0xFF00BFA5) else Color(0xFFE91E63),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onToggleWatched() }
                ) {
                    Text(
                        text = if (movie.isWatched) "Đã xem" else "Chưa xem",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin phim ở giữa
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.genre,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color.Yellow, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = movie.rating.toString(), color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${movie.runtime} phút", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // Dấu X xóa phim (chỉ hiện khi ở chế độ sửa)
            if (isEditMode) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, avatarUrl: String, onLogout: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF23304B)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNotEmpty()) {
                    AsyncImage(model = avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Text("Ảnh", color = Color.Cyan, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23304B)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Đăng xuất", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun GenreDistributionSection(genreDistribution: Map<String, Int>, totalTime: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(0.35f)) {
                CustomPieChart(data = genreDistribution, modifier = Modifier.size(80.dp))
                Text("Thể loại", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Column(modifier = Modifier.weight(0.45f).padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow, Color.Green, Color.Red)
                genreDistribution.keys.take(4).forEachIndexed { index, genre ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(colors[index % colors.size], CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = genre, color = Color.White, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Column(modifier = Modifier.weight(0.2f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tổng TG", color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.Center)
                Text(text = "$totalTime", color = Color.Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("phút", color = Color.Gray, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun CustomPieChart(data: Map<String, Int>, modifier: Modifier = Modifier) {
    val colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow, Color.Green, Color.Red)
    val totalCount = data.values.sum().toFloat().let { if (it == 0f) 1f else it }
    Canvas(modifier = modifier) {
        var startAngle = -90f
        if (data.isEmpty()) {
            drawCircle(Color.Gray)
        } else {
            data.values.forEachIndexed { index, count ->
                val sweepAngle = (count / totalCount) * 360f
                drawArc(color = colors[index % colors.size], startAngle = startAngle, sweepAngle = sweepAngle, useCenter = true)
                startAngle += sweepAngle
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B101B)
@Composable
fun ProfileScreenPreview() {
    val dummyUser = UserEntity(name = "Nguyễn Văn A", avatarUrl = "")
    val dummyMovies = listOf(
        FavoriteMovieEntity(1, "Inception", "/edv5bs1pUQC67SWHqcYf67OQ97R.jpg", "", "Hành động", 8.8f, 148, true),
        FavoriteMovieEntity(2, "The Dark Knight", "/qJ2tW6WMUDp9QmSJJIVP6YFZO8r.jpg", "", "Hành động", 9.0f, 152, true),
        FavoriteMovieEntity(3, "Interstellar", "/gEU2QniE6E77NI6lCU6MxlS67jP.jpg", "", "Khoa học", 8.7f, 169, false)
    )
    
    ProfileScreenPreviewContent(dummyUser, dummyMovies)
}

@Composable
fun ProfileScreenPreviewContent(user: UserEntity, movies: List<FavoriteMovieEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0B101B)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ProfileHeader(user.name, "", {}) }
        item { GenreDistributionSection(mapOf("Hành động" to 2, "Khoa học" to 1), 300) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Danh sách yêu thích", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Sửa", color = Color.Cyan, fontSize = 12.sp)
            }
        }
        items(movies) { HorizontalFavoriteMovieItem(it, true, {}, {}) }
    }
}
