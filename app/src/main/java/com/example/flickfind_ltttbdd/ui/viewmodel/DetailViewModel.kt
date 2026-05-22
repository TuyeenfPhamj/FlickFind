package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import com.google.firebase.auth.FirebaseAuth
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

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // 1. Kiểm tra trạng thái yêu thích trước (Dùng để hiển thị tim ngay lập tức)
            val isFav = if (currentUserId.isNotEmpty()) {
                repository.isMovieFavorite(movieId, currentUserId)
            } else false
            _uiState.update { it.copy(isFavorite = isFav) }

            // 2. Lấy dữ liệu chi tiết từ API
            val result = repository.getMovieByIdFromApi(movieId)
            result.onSuccess { movie ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movie = movie, 
                        errorMessage = null
                    ) 
                }
            }.onFailure { error ->
                // Xử lý khi API lỗi 404 (Thường do MockAPI bị giới hạn dữ liệu chi tiết)
                
                // Cố gắng tìm thông tin phim từ danh sách phim trang chủ đã tải (nếu có)
                // Hoặc từ danh sách yêu thích
                val localData = if (isFav) {
                    repository.getAllFavorites(currentUserId).first().find { it.id == movieId }
                } else null

                if (localData != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movie = MovieResponse(
                                id = localData.id,
                                title = localData.title,
                                posterPath = localData.posterPath,
                                backdropPath = localData.backdropPath,
                                genres = localData.genre.split(",").map { g -> g.trim() },
                                rating = localData.rating,
                                runtime = localData.runtime,
                                director = "Không có dữ liệu",
                                cast = "Không có dữ liệu",
                                releaseDate = "",
                                overview = "Thông tin chi tiết hiện không khả dụng từ máy chủ, nhưng bạn vẫn có thể xem thông tin cơ bản của phim này."
                            )
                        )
                    }
                } else {
                    // Nếu không có trong database, ta vẫn thử lấy thông tin từ list 100 phim để hiển thị
                    // nhằm tránh hiện lỗi 404 gây khó chịu cho người dùng
                    repository.getMoviesFromApi(1, 100).onSuccess { allMovies ->
                        val movieInList = allMovies.find { it.id == movieId }
                        if (movieInList != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    movie = movieInList,
                                    errorMessage = null
                                )
                            }
                        } else {
                            val msg = error.localizedMessage ?: "Lỗi kết nối máy chủ (404)"
                            _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                        }
                    }.onFailure {
                        val msg = error.localizedMessage ?: "Không tìm thấy phim này (404)"
                        _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentMovie = _uiState.value.movie ?: return
        if (currentUserId.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng đăng nhập để lưu phim yêu thích") }
            return
        }

        viewModelScope.launch {
            val isFav = _uiState.value.isFavorite
            val entity = FavoriteMovieEntity(
                id = currentMovie.id,
                userId = currentUserId,
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
