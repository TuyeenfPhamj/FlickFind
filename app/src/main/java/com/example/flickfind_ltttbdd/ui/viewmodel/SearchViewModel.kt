package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val movies: List<MovieResponse> = emptyList(),
    val favoriteMovieIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val query: String? = null,
    val genre: String? = null,
    val yearRange: String? = null,
    val sortBy: String? = null,
    val isEndReached: Boolean = false
)

class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentSearchJob: Job? = null
    private var favoritesJob: Job? = null

    // [GHI CHÚ]: Lắng nghe thay đổi tài khoản để cập nhật icon Trái tim thời gian thực
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val userId = auth.currentUser?.uid ?: "guest_user"
        observeFavorites(userId)
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    private fun observeFavorites(userId: String) {
        favoritesJob?.cancel()
        favoritesJob = viewModelScope.launch {
            repository.getAllFavorites(userId).collect { favoriteEntities ->
                _uiState.update { currentState ->
                    currentState.copy(favoriteMovieIds = favoriteEntities.map { it.id }.toSet())
                }
            }
        }
    }

    fun setFiltersAndSearch(query: String?, genre: String?, yearRange: String?, sortBy: String? = null) {
        val cleanQuery = query?.takeIf { it.isNotBlank() }
        val cleanGenre = genre?.takeIf { it.isNotBlank() }
        val cleanYear = yearRange?.takeIf { it.isNotBlank() }
        val cleanSort = sortBy?.takeIf { it.isNotBlank() }

        _uiState.update { 
            it.copy(
                query = cleanQuery, 
                genre = cleanGenre, 
                yearRange = cleanYear,
                sortBy = cleanSort,
                movies = emptyList(),
                isEndReached = false,
                isLoading = false,
                errorMessage = null
            )
        }
        
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            performSearch()
        }
    }

    private suspend fun performSearch() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val result = repository.getMoviesFromApi(
            page = 1,
            limit = 100,
            search = null
        )

        result.onSuccess { allMovies ->
            val filtered = allMovies.filter { movie ->
                val q = _uiState.value.query
                val g = _uiState.value.genre
                val y = _uiState.value.yearRange

                val matchQuery = q == null || movie.title.contains(q, ignoreCase = true)
                val matchGenre = g == null || movie.genres.any { it.contains(g, ignoreCase = true) }
                val matchYear = y == null || matchesYearRange(movie.releaseDate, y)

                matchQuery && matchGenre && matchYear
            }

            val sorted = if (_uiState.value.sortBy == "rating") {
                filtered.sortedByDescending { it.rating }
            } else {
                filtered
            }

            _uiState.update { it.copy(
                isLoading = false, 
                movies = sorted,
                isEndReached = true
            ) }
        }.onFailure { e ->
            _uiState.update { it.copy(
                isLoading = false, 
                errorMessage = e.localizedMessage ?: "Lỗi kết nối máy chủ"
            ) }
        }
    }

    private fun matchesYearRange(releaseDate: String, range: String): Boolean {
        return try {
            val years = range.split("-").map { it.trim().toIntOrNull() }
            val start = years.getOrNull(0) ?: 0
            val end = years.getOrNull(1) ?: 9999
            
            val movieYear = releaseDate.take(4).toIntOrNull() ?: 0
            movieYear in start..end
        } catch (e: Exception) {
            true 
        }
    }

    fun loadNextMovies() {}

    fun toggleFavorite(movie: MovieResponse) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest_user"
        viewModelScope.launch {
            val isFav = repository.isMovieFavorite(movie.id, userId)
            val entity = FavoriteMovieEntity(
                id = movie.id,
                userId = userId,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                genre = movie.genres.joinToString(", "),
                rating = movie.rating,
                runtime = movie.runtime,
                isWatched = false
            )

            if (isFav) {
                repository.removeFromFavorite(entity)
            } else {
                repository.addToFavorite(entity)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }
}
