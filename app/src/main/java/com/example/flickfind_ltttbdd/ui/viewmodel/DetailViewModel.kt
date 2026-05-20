package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DetailUiState(
    val movie: MovieResponse? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val repository: MovieRepository,
    private val movieId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 1. Kiểm tra trong Database trước
            val favorites = repository.getAllFavorites().first()
            val localMovie = favorites.find { it.id == movieId }
            
            if (localMovie != null) {
                _uiState.update { 
                    it.copy(
                        isFavorite = true,
                        movie = MovieResponse(
                            id = localMovie.id,
                            title = localMovie.title,
                            posterPath = localMovie.posterPath,
                            backdropPath = localMovie.backdropPath,
                            genres = localMovie.genre.split(",").map { g -> g.trim() },
                            rating = localMovie.rating,
                            runtime = localMovie.runtime,
                            director = "Đang tải...",
                            cast = "Đang tải...",
                            releaseDate = "",
                            overview = "Đang tải thông tin chi tiết..."
                        )
                    )
                }
            }

            // 2. Lấy dữ liệu mới nhất từ API
            val result = repository.getMovieByIdFromApi(movieId)
            result.onSuccess { movie ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movie = movie, 
                        errorMessage = null,
                        isFavorite = localMovie != null || it.isFavorite
                    ) 
                }
            }.onFailure { error ->
                // Nếu lỗi 404 nhưng đã có dữ liệu local thì vẫn cho xem
                if (localMovie != null) {
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    val msg = error.localizedMessage ?: "Không tìm thấy phim này trên hệ thống (404)"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentMovie = _uiState.value.movie ?: return
        viewModelScope.launch {
            val isFav = _uiState.value.isFavorite
            val entity = FavoriteMovieEntity(
                id = currentMovie.id,
                title = currentMovie.title,
                posterPath = currentMovie.posterPath,
                backdropPath = currentMovie.backdropPath,
                genre = currentMovie.genres.joinToString(", "),
                rating = currentMovie.rating,
                runtime = currentMovie.runtime,
                isWatched = false
            )

            if (isFav) {
                repository.removeFromFavorite(entity)
            } else {
                repository.addToFavorite(entity)
            }
            _uiState.update { it.copy(isFavorite = !isFav) }
        }
    }
}
