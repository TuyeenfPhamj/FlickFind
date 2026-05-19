package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.flickfind_ltttbdd.navigation.Screen
import com.example.flickfind_ltttbdd.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    query: String?,
    genre: String?,
    yearRange: String?,
    viewModel: SearchViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    // Gọi API lọc mới mỗi khi tham số đầu vào thay đổi
    LaunchedEffect(query, genre, yearRange) {
        viewModel.setFiltersAndSearch(query, genre, yearRange)
    }

    // Tự động tải thêm khi cuộn gần tới cuối danh sách
    val movies = uiState.movies

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Kết quả tìm kiếm", color = Color.White, fontSize = 18.sp)
                        val filterText = listOfNotNull(
                            query?.takeIf { it.isNotBlank() }?.let { "Từ khóa: $it" },
                            genre?.takeIf { it.isNotBlank() }?.let { "Thể loại: $it" },
                            yearRange?.takeIf { it.isNotBlank() }?.let { "Năm: $it" }
                        ).joinToString(" | ")
                        if (filterText.isNotBlank()) {
                            Text(filterText, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B121F))
            )
        },
        containerColor = Color(0xFF0B121F)
    ) { padding ->
        if (movies.isEmpty() && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy phim nào phù hợp", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    MovieHorizontalRowItem(
                        movie = movie,
                        isFavorite = uiState.favoriteMovieIds.contains(movie.id),
                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                        onCardClick = {
                            navController.navigate(Screen.Detail.createRoute(movie.id))
                        }
                    )
                }

                // Hiển thị loading khi tải trang tiếp theo
                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF38B6FF))
                        }
                    }
                }

                // Trigger tải thêm phim khi cuộn tới cuối
                item {
                    LaunchedEffect(Unit) {
                        if (!uiState.isEndReached && !uiState.isLoading) {
                            viewModel.loadNextMovies()
                        }
                    }
                }
            }
        }
    }
}
