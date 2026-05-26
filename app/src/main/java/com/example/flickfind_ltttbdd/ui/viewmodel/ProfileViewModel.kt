package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.local.UserEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserEntity? = null,
    val favoriteMovies: List<FavoriteMovieEntity> = emptyList(),
    val totalWatchTime: Int = 0,
    val mostWatchedGenre: String = "Chưa có",
    val leastWatchedGenre: String = "Chưa có"
)

class ProfileViewModel(
    private val repository: MovieRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        // Theo dõi thông tin User
        viewModelScope.launch {
            repository.getUserProfile(userId).collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        // Theo dõi danh sách phim yêu thích và tính toán thống kê
        viewModelScope.launch {
            repository.getAllFavorites(userId).collect { favorites ->
                val watchedMovies = favorites.filter { it.isWatched }
                
                // 1. Tính tổng thời gian đã xem (phút)
                val totalTime = watchedMovies.sumOf { it.runtime }
                
                // 2. Phân tích thể loại từ danh sách phim đã xem
                val genreCounts = watchedMovies
                    .flatMap { it.genre.split(",").map { g -> g.trim() } }
                    .filter { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()
                
                val mostWatched = genreCounts.maxByOrNull { it.value }?.key ?: "Chưa có"
                val leastWatched = genreCounts.minByOrNull { it.value }?.key ?: "Chưa có"

                _uiState.update { 
                    it.copy(
                        favoriteMovies = favorites,
                        totalWatchTime = totalTime,
                        mostWatchedGenre = mostWatched,
                        leastWatchedGenre = leastWatched
                    )
                }
            }
        }
    }

    // Đánh dấu đã xem/chưa xem
    fun toggleWatched(movie: FavoriteMovieEntity) {
        viewModelScope.launch {
            repository.addToFavorite(movie.copy(isWatched = !movie.isWatched))
        }
    }
    
    // Xóa khỏi danh sách yêu thích
    fun deleteFavorite(movie: FavoriteMovieEntity) {
        viewModelScope.launch {
            repository.removeFromFavorite(movie)
        }    }
}
