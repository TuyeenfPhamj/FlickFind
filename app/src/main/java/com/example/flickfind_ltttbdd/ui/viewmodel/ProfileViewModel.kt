package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val user: UserEntity? = null,
    val favoriteMovies: List<FavoriteMovieEntity> = emptyList(),
    val genreDistribution: Map<String, Int> = emptyMap(),
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        
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
                        genreDistribution = genreCounts,
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
        }
    }

    fun updateAvatar(url: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        
        viewModelScope.launch {
            try {
                // 1. Cập nhật Firebase Auth profile
                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(android.net.Uri.parse(url))
                        .build()
                ).await()
                
                // 2. Cập nhật Local Room Database
                repository.insertOrUpdateUser(
                    UserEntity(
                        id = currentUser.uid,
                        name = currentUser.displayName ?: "Người dùng",
                        avatarUrl = url
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateName(name: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        
        viewModelScope.launch {
            try {
                // 1. Cập nhật Firebase Auth profile
                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                ).await()
                
                // 2. Cập nhật Local Room Database
                repository.insertOrUpdateUser(
                    UserEntity(
                        id = currentUser.uid,
                        name = name,
                        avatarUrl = currentUser.photoUrl?.toString() ?: ""
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
