package com.example.flickfind_ltttbdd.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    sortBy: String?,
    viewModel: SearchViewModel,
    navController: NavController,
    isDarkTheme: Boolean
) {
    // [GHI CHÚ]: Đồng bộ màu sắc theo theme Sáng/Tối
    val backgroundColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF172033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val primaryColor = Color(0xFF38B6FF)

    val uiState by viewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 2 else 1

    LaunchedEffect(query, genre, yearRange, sortBy) {
        viewModel.setFiltersAndSearch(query, genre, yearRange, sortBy)
    }

    val movies = uiState.movies

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Kết quả tìm kiếm", color = textColor, fontSize = 18.sp)
                        val filterText = listOfNotNull(
                            query?.takeIf { it.isNotBlank() }?.let { "Từ khóa: $it" },
                            genre?.takeIf { it.isNotBlank() }?.let { "Thể loại: $it" },
                            yearRange?.takeIf { it.isNotBlank() }?.let { "Năm: $it" }
                        ).joinToString(" | ")
                        if (filterText.isNotBlank()) {
                            Text(filterText, color = if (isDarkTheme) Color.Gray else Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        if (movies.isEmpty() && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Không tìm thấy phim nào phù hợp", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    MovieHorizontalRowItem(
                        movie = movie,
                        isFavorite = uiState.favoriteMovieIds.contains(movie.id),
                        cardColor = cardColor,
                        textColor = textColor,
                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                        onCardClick = {
                            navController.navigate(Screen.Detail.createRoute(movie.id))
                        }
                    )
                }

                if (uiState.isLoading) {
                    item(span = { GridItemSpan(columns) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    }
                }

                item(span = { GridItemSpan(columns) }) {
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
