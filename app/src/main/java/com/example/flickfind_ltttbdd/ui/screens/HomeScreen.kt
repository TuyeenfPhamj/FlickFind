package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.R
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
    var isSuggestionsVisible by remember { mutableStateOf(false) }

    // Xử lý Debounce tìm kiếm: Chỉ tìm khi nhập > 3 ký tự và dừng gõ 2 giây
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 3) {
            kotlinx.coroutines.delay(2000)
            viewModel.updateSearchSuggestions(searchQuery)
            isSuggestionsVisible = true
        } else {
            viewModel.clearSuggestions()
            isSuggestionsVisible = false
        }
    }

    // Giao diện tổng thể sử dụng LazyColumn để cuộn mượt mà toàn bộ trang chủ
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B121F))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    Image(
                        painter = painterResource(id = R.drawable.logo_v1),
                        contentDescription = "Logo FlickFind",
                        modifier = Modifier
                            .width(340.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 2. THANH TÌM KIẾM (Search Bar)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            // Trình điều khiển hiển thị gợi ý đã được xử lý bởi LaunchedEffect (Debounce)
                            // Tuy nhiên, khi xóa text về <= 3 thì ẩn ngay lập tức cho trải nghiệm mượt mà
                            if (it.length <= 3) {
                                isSuggestionsVisible = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Thanh tìm kiếm...", color = Color.Gray) },
                        leadingIcon = {
                            IconButton(onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    navController.navigate(Screen.SearchResult.createRoute(query = searchQuery))
                                }
                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { navController.navigate(Screen.Filter.route) }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38B6FF),
                            unfocusedBorderColor = Color(0xFF233044),
                            focusedContainerColor = Color(0xFF131C2E),
                            unfocusedContainerColor = Color(0xFF131C2E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    navController.navigate(Screen.SearchResult.createRoute(query = searchQuery))
                                    isSuggestionsVisible = false
                                }
                            }
                        )
                    )

                    // Hiển thị danh sách gợi ý ngay bên dưới thanh tìm kiếm (Dropdown overlay style)
                    if (isSuggestionsVisible && uiState.searchSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column {
                                uiState.searchSuggestions.forEach { movie ->
                                    Text(
                                        text = movie.title,
                                        color = Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(Screen.Detail.createRoute(movie.id))
                                                isSuggestionsVisible = false
                                            }
                                            .padding(12.dp),
                                        fontSize = 14.sp
                                    )
                                    HorizontalDivider(color = Color(0xFF0B121F), thickness = 1.dp)
                                }

                                // Nút màu xanh (Xem tất cả kết quả)
                                Button(
                                    onClick = {
                                        navController.navigate(Screen.SearchResult.createRoute(query = searchQuery))
                                        isSuggestionsVisible = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Màu xanh lá như yêu cầu
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                ) {
                                    Text("Xem tất cả kết quả", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Giao diện trang chủ không thay đổi theo searchQuery (Dùng uiState.movies gốc)
            val allMovies = uiState.movies

            // 3. MỤC PHIM PHỔ BIẾN
            if (allMovies.isNotEmpty()) {
                item {
                    Text(
                        text = "Phim Phổ Biến",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val popularMovies = allMovies.take(5)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(popularMovies) { movie ->
                            MovieItemCard(
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
            }

            // 4. MỤC PHIM HOT
            if (allMovies.isNotEmpty()) {
                item {
                    Text(
                        text = "Phim Hot",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(allMovies) { index, movie ->
                    if (index >= allMovies.lastIndex - 2 && !uiState.isLoading && !uiState.isEndReached) {
                        LaunchedEffect(key1 = allMovies.size) {
                            viewModel.loadNextMovies()
                        }
                    }

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

            // 5. TRẠNG THÁI LOADING
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