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
import androidx.compose.ui.platform.LocalConfiguration
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
    navController: NavController,
    isDarkTheme: Boolean
) {
    // [GHI CHÚ]: Đồng bộ màu sắc theo theme Sáng/Tối
    val backgroundColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF172033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val searchBarColor = if (isDarkTheme) Color(0xFF131C2E) else Color.White

    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSuggestionsVisible by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columns = if (isLandscape) 2 else 1

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

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(columns) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = if (isDarkTheme) R.drawable.logo_v1 else R.drawable.logo_v4),
                        contentDescription = "Logo FlickFind",
                        modifier = Modifier
                            .width(340.dp)
                            .height(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

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
                            unfocusedBorderColor = if (isDarkTheme) Color(0xFF233044) else Color.LightGray,
                            focusedContainerColor = searchBarColor,
                            unfocusedContainerColor = searchBarColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
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

                    if (isSuggestionsVisible && uiState.searchSuggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = searchBarColor),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Column {
                                uiState.searchSuggestions.forEach { movie ->
                                    Text(
                                        text = movie.title,
                                        color = textColor,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                navController.navigate(Screen.Detail.createRoute(movie.id))
                                                isSuggestionsVisible = false
                                            }
                                            .padding(12.dp),
                                        fontSize = 14.sp
                                    )
                                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                                }

                                Button(
                                    onClick = {
                                        navController.navigate(Screen.SearchResult.createRoute(query = searchQuery))
                                        isSuggestionsVisible = false
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

            if (allMovies.isNotEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Phim Phổ Biến",
                            color = textColor,
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
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(popularMovies) { movie ->
                            MovieItemCard(
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
                    }
                }
            }

            if (allMovies.isNotEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    Text(
                        text = "Phim Hot",
                        color = textColor,
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
                            cardColor = cardColor,
                            textColor = textColor,
                            onFavoriteClick = { viewModel.toggleFavorite(movie) },
                            onCardClick = {
                                navController.navigate(Screen.Detail.createRoute(movie.id))
                            }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item(span = { GridItemSpan(columns) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF38B6FF))
                    }
                }
            }

            uiState.errorMessage?.let { error ->
                item(span = { GridItemSpan(columns) }) {
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

@Composable
fun MovieItemCard(
    movie: MovieResponse,
    isFavorite: Boolean,
    cardColor: Color,
    textColor: Color,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(text = movie.title, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
    cardColor: Color,
    textColor: Color,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(text = movie.title, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = movie.genres.joinToString(", "), color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "★ ${movie.rating}", color = Color(0xFFFFC107), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else (if (textColor == Color.White) Color.White else Color.Black)
                    )
                }
            }
        }
    }
}
