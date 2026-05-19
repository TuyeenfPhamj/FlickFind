package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    val bgColor = Color(0xFF0B101B) // Màu nền tối toàn màn hình

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        // 1. Header: Avatar + Tên + Đăng xuất
        ProfileHeader(
            name = uiState.user?.name ?: "Người dùng",
            avatarUrl = uiState.user?.avatarUrl ?: "",
            onLogout = onLogout
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Statistics: Phim xem nhiều nhất/ít nhất + Tổng thời gian
        StatsSection(
            mostWatched = uiState.mostWatchedGenre,
            leastWatched = uiState.leastWatchedGenre,
            totalTime = uiState.totalWatchTime
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Title Section: Danh sách yêu thích
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Danh sách yêu thích",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { /* Sửa danh sách */ }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF172033), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Sửa", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Favorite List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.favoriteMovies) { movie ->
                FavoriteMovieItem(
                    movie = movie,
                    onToggleWatched = { viewModel.toggleWatched(movie) },
                    onDelete = { viewModel.deleteFavorite(movie) }
                )
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh đại diện
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF23304B)),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Ảnh\nđại\ndiện", color = Color.Cyan, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tên tài khoản
            Text(
                text = name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f) // Dùng weight để đẩy nút Đăng xuất
            )

            // Nút đăng xuất
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23304B)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Đăng\nxuất", color = Color.White, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun StatsSection(mostWatched: String, leastWatched: String, totalTime: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cột bên trái: Phim xem nhiều nhất/ít nhất
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.6f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Thể loại xem nhiều nhất: $mostWatched", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Thể loại xem ít nhất: $leastWatched", color = Color.White, fontSize = 12.sp)
            }
        }

        // Cột bên phải: Tổng thời gian
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.4f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tổng thời gian đã xem", color = Color.White, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text(text = "$totalTime phút", color = Color.Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FavoriteMovieItem(
    movie: FavoriteMovieEntity,
    onToggleWatched: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ảnh phim
            Box(modifier = Modifier.width(100.dp)) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Label Đã xem/Chưa xem
                Surface(
                    color = if (movie.isWatched) Color(0xFF00BFA5) else Color(0xFF23304B),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    onClick = onToggleWatched
                ) {
                    Text(
                        text = if (movie.isWatched) "Đã xem" else "Chưa xem",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin phim
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = movie.genre,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = movie.rating.toString(), color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B101B)
@Composable
fun ProfileScreenPreview() {
    val dummyUser = UserEntity(name = "Nguyễn Văn A", avatarUrl = "")
    val dummyMovies = listOf(
        FavoriteMovieEntity(
            id = 1,
            title = "Inception",
            posterPath = "/edv5bs1pUQC67SWHqcYf67OQ97R.jpg",
            backdropPath = "",
            genre = "Hành động, Khoa học viễn tưởng",
            rating = 8.8f,
            runtime = 148,
            isWatched = true
        ),
        FavoriteMovieEntity(
            id = 2,
            title = "The Dark Knight",
            posterPath = "/qJ2tW6WMUDp9QmSJJIVP6YFZO8r.jpg",
            backdropPath = "",
            genre = "Hành động, Hình sự",
            rating = 9.0f,
            runtime = 152,
            isWatched = false
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B101B))
            .padding(16.dp)
    ) {
        ProfileHeader(name = dummyUser.name, avatarUrl = dummyUser.avatarUrl, onLogout = {})
        Spacer(modifier = Modifier.height(20.dp))
        StatsSection(mostWatched = "Hành động", leastWatched = "Kinh dị", totalTime = 300)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Danh sách yêu thích", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(dummyMovies) { movie ->
                FavoriteMovieItem(movie = movie, onToggleWatched = {}, onDelete = {})
            }
        }
    }
}
