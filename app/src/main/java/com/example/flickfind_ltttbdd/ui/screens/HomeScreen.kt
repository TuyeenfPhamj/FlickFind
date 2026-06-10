package com.example.flickfind_ltttbdd.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    // Kiểm tra hướng màn hình để quyết định số cột (Dọc: 1 cột, Ngang: 2 cột)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 2 else 1

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

    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            isRefreshing = false
        }
    }

    // Giao diện tổng thể
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0B121F))) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refreshMovies()
            },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. KHỐI LOGO (Căn giữa) - Chiếm hết số cột
                item(span = { GridItemSpan(columns) }) {
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

                // 2. THANH TÌM KIẾM (Search Bar) - Chiếm hết số cột
                item(span = { GridItemSpan(columns) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                if (it.length <= 3) {
                                    isSuggestionsVisible = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Thanh tìm kiếm...", color = Color.Gray) },
                            leadingIcon = {
                                IconButton(onClick = {
                                    val cleanQuery = searchQuery.trim()
                                    if (cleanQuery.isNotEmpty()) {
                                        navController.navigate(Screen.SearchResult.createRoute(query = cleanQuery))
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
                                    val cleanQuery = searchQuery.trim()
                                    if (cleanQuery.isNotBlank()) {
                                        navController.navigate(Screen.SearchResult.createRoute(query = cleanQuery))
                                        isSuggestionsVisible = false
                                    }
                                }
                            )
                        )

                        // Hiển thị danh sách gợi ý
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

                                    Button(
                                        onClick = {
                                            val cleanQuery = searchQuery.trim()
                                            if (cleanQuery.isNotBlank()) {
                                                navController.navigate(Screen.SearchResult.createRoute(query = cleanQuery))
                                                isSuggestionsVisible = false
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                                    ) {
                                        Text("Xem tất cả kết quả", color = Color.White, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                val allMovies = uiState.movies

                // 3. MỤC PHIM PHỔ BIẾN
                if (allMovies.isNotEmpty()) {
                    item(span = { GridItemSpan(columns) }) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Phim Phổ Biến",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "xem thêm...",
                                    color = Color(0xFF38B6FF),
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.SearchResult.createRoute(sortBy = "rating"))
                                    }
                                )
                            }

                            val popularMovies = uiState.popularMovies
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxWidth()
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
                }

                // 4. MỤC PHIM HOT
                if (allMovies.isNotEmpty()) {
                    item(span = { GridItemSpan(columns) }) {
                        Text(
                            text = "Phim Hot",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
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
                if (uiState.isLoading && !isRefreshing) {
                    item(span = { GridItemSpan(columns) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF38B6FF))
                        }
                    }
                }

                // 6. THÔNG BÁO LỖI
                uiState.errorMessage?.let { error ->
                    item(span = { GridItemSpan(columns) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

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
                AsyncImage(
                    model = movie.posterPath,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )
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
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = movie.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = movie.genres.joinToString(", "), color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "★ ${movie.rating}", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

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
                    .weight(1f)
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
