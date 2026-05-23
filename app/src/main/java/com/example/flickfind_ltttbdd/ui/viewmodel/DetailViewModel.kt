package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _movie = MutableStateFlow<MovieResponse?>(null)
    val movie: StateFlow<MovieResponse?> = _movie

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val userId = "user_1" // Mặc định userId

    fun getMovieById(movieId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getMovieByIdFromApi(movieId)
                .onSuccess {
                    _movie.value = it
                    checkIfFavorite(it.id)
                    _isLoading.value = false
                }
                .onFailure {
                    _error.value = "Không thể tải thông tin phim"
                    _isLoading.value = false
                }
        }
    }

    private fun checkIfFavorite(movieId: Int) {
        viewModelScope.launch {
            _isFavorite.value = repository.isMovieFavorite(movieId, userId)
        }
    }

    fun toggleFavorite(movie: MovieResponse) {
        viewModelScope.launch {
            val favoriteMovie = FavoriteMovieEntity(
                id = movie.id,
                userId = userId,
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