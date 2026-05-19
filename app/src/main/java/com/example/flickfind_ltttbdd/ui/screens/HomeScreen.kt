package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import com.example.flickfind_ltttbdd.navigation.Screen
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    // Lắng nghe trạng thái UI State từ ViewModel phát ra
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Giao diện tổng thể sử dụng LazyColumn để cuộn mượt mà toàn bộ trang chủ
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B121F)), // Màu nền tối sâu theo đúng thiết kế wireframe
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // 1. KHỐI LOGO (Căn giữa)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFF1A2436),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(140.dp).height(45.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "FlickFind",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. THANH TÌM KIẾM (Search Bar)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Thanh tìm kiếm...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38B6FF),
                    unfocusedBorderColor = Color(0xFF233044),
                    focusedContainerColor = Color(0xFF131C2E),
                    unfocusedContainerColor = Color(0xFF131C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        // Lọc danh sách phim dựa theo thanh tìm kiếm (nếu có nhập)
        val filteredMovies = uiState.movies.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }

        // 3. MỤC PHIM PHỔ BIẾN (Hiển thị 5 phim đầu tiên dạng Cuộn Ngang - LazyRow)
        if (filteredMovies.isNotEmpty()) {
            item {
                Text(
                    text = "Phim Phổ Biến",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val popularMovies = filteredMovies.take(5) // Lấy tối đa 5 bản ghi làm phim phổ biến
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(popularMovies) { movie ->
                        MovieItemCard(
                            movie = movie,
                            isFavorite = uiState.favoriteMovieIds.contains(movie.id),
                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                            onCardClick = {
                                // Điều hướng sang màn hình Chi tiết truyền kèm ID phim
                                navController.navigate(Screen.Detail.createRoute(movie.id))
                            }
                        )
                    }
                }
            }
        }

        // 4. MỤC PHIM HOT (Hiển thị toàn bộ danh sách còn lại dạng Cuộn Dọc & Phân Trang)
        if (filteredMovies.isNotEmpty()) {
            item {
                Text(
                    text = "Phim Hot",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Duyệt danh sách phim kèm chỉ số (index) để phát hiện thời điểm chạm đáy màn hình
            itemsIndexed(filteredMovies) { index, movie ->
                // THUẬT TOÁN PHÂN TRANG: Nếu người dùng cuộn đến vị trí cách phần tử cuối cùng 2 mục,
                // hệ thống tự động kích hoạt lệnh gọi API tải thêm trang tiếp theo.
                if (index >= filteredMovies.lastIndex - 2 && !uiState.isLoading && !uiState.isEndReached) {
                    LaunchedEffect(key1 = filteredMovies.size) {
                        viewModel.loadNextMovies()
                    }
                }

                // Vẽ hàng phim Hot (Mỗi dòng một thẻ phim thiết kế chuẩn wireframe)
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    MovieHorizontalRowItem(
                        movie = movie,
                        isFavorite = uiState.favoriteMovieIds.contains(movie.id),
                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                        onCardClick = {
                            navController.navigate(Screen.Detail.createRoute(movie.id))
                        }
                    )
                }
            }
        }

        // 5. TRẠNG THÁI LOADING / BÁO LỖI Ở ĐÁY MÀN HÌNH
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF38B6FF))
                }
            }
        }

        uiState.errorMessage?.let { error ->
            item {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
}

// COMPONENTS 1: Thẻ Phim Cuộn Ngang (Phim Phổ Biến)
@Composable
fun MovieItemCard(
    movie: MovieResponse,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                // Thư viện Coil tự tải ảnh từ URL cực kỳ mượt mà
                AsyncImage(
                    model = movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )
                // Nút trái tim yêu thích bọc trên góc ảnh
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
            // Khối nội dung chữ bên dưới ảnh giống y hệt Wireframe
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = movie.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = movie.genres.joinToString(", "), color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "★ ${movie.rating}", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// COMPONENTS 2: Dòng Phim Cuộn Dọc (Phim Hot)
@Composable
fun MovieHorizontalRowItem(
    movie: MovieResponse,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier.width(80.dp).fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(100f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = movie.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = movie.genres.joinToString(", "), color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "★ ${movie.rating}", color = Color(0xFFFFC107), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }
        }
    }
}