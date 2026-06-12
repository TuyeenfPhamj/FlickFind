package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movie = MutableStateFlow<MovieResponse?>(null)
    val movie: StateFlow<MovieResponse?> = _movie

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getMovieById(movieId: String) {
        if (movieId.isBlank()) {
            _error.value = "ID phim không hợp lệ"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Thay vì gọi trực tiếp endpoint /{id} (dễ bị 404 trên MockAPI),
            // chúng ta lấy danh sách và lọc theo trường id trong JSON.
            repository.getMoviesFromApi(page = 1, limit = 100)
                .onSuccess { movies ->
                    val foundMovie = movies.find { it.id == movieId }
                    if (foundMovie != null) {
                        _movie.value = foundMovie
                        observeFavoriteStatus(foundMovie.id)
                        _isLoading.value = false
                    } else {
                        _error.value = "Không tìm thấy phim có ID: $movieId"
                        _isLoading.value = false
                    }
                }
                .onFailure { exception ->
                    android.util.Log.e("DetailViewModel", "Lỗi tải phim", exception)
                    _error.value = "Lỗi kết nối hoặc không tìm thấy phim"
                    _isLoading.value = false
                }
        }
    }

    // [GHI CHÚ]: Theo dõi trạng thái yêu thích dựa trên userId hiện tại
    private fun observeFavoriteStatus(movieId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _isFavorite.value = false
            return
        }
        viewModelScope.launch {
            repository.getAllFavorites(currentUser.uid).collect { favorites ->
                _isFavorite.value = favorites.any { it.id == movieId }
            }
        }
    }

    // [GHI CHÚ]: Thêm/Xóa phim khỏi danh sách yêu thích liên kết với từng tài khoản
    fun toggleFavorite(movie: MovieResponse) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _error.value = "Vui lòng đăng nhập để thích phim"
            return
        }
        val userId = currentUser.uid
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
            } else {
                repository.addToFavorite(favoriteMovie)
            }
        }
    }
}
