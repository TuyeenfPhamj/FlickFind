package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _movie = MutableStateFlow<MovieResponse?>(null)
    val movie: StateFlow<MovieResponse?> = _movie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun getMovieById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getMovieById(id)
                .onSuccess { movieResponse ->
                    _movie.value = movieResponse
                    checkIfFavorite(movieResponse.id)
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Lỗi khi tải chi tiết phim"
                    _isLoading.value = false
                }
        }
    }

    private fun checkIfFavorite(movieId: Int) {
        viewModelScope.launch {
            _isFavorite.value = repository.isMovieFavorite(movieId)
        }
    }

    fun toggleFavorite(movie: MovieResponse) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovieEntity(
                id = movie.id,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                genre = movie.genres.joinToString(", "),
                rating = movie.rating,
                runtime = movie.runtime
            )
            if (_isFavorite.value) {
                repository.removeFromFavorite(favoriteMovie)
                _isFavorite.value = false
            } else {
                repository.addToFavorite(favoriteMovie)
                _isFavorite.value = true
            }
        }
    }
}
