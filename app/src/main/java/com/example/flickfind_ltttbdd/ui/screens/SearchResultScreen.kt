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
import com.example.flickfind_ltttbdd.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    query: String?,
    genre: String?,
    yearRange: String?,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Logic lọc phim dựa trên các tiêu chí
    val filteredMovies = remember(uiState.movies, query, genre, yearRange) {
        uiState.movies.filter { movie ->
            val matchQuery = query.isNullOrBlank() || movie.title.contains(query, ignoreCase = true)
            val matchGenre = genre.isNullOrBlank() || movie.genres.any { it.contains(genre, ignoreCase = true) }
            val matchYear = yearRange.isNullOrBlank() || try {
                val years = yearRange.split("-").map { it.trim().toInt() }
                val movieYear = movie.releaseDate.take(4).toInt()
                movieYear in years[0]..years[1]
            } catch (e: Exception) { true }
            
            matchQuery && matchGenre && matchYear
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Kết quả tìm kiếm", color = Color.White, fontSize = 18.sp)
                        if (!query.isNullOrBlank()) {
                            Text("Từ khóa: $query", color = Color.Gray, fontSize = 12.sp)
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
        if (filteredMovies.isEmpty()) {
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
                items(filteredMovies) { movie ->
                    MovieHorizontalRowItem(
                        movie = movie,
                        isFavorite = uiState.favoriteMovieIds.contains(movie.id),
                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                        onCardClick = {
                            navController.navigate("detail/${movie.id}")
                        }
                    )
                }
            }
        }
    }
}
