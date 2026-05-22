package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import android.net.Uri
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
    val totalWatchTime: Int = 0,
    val genreDistribution: Map<String, Int> = emptyMap()
)

class ProfileViewModel(private val repository: MovieRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        checkAndSyncUser()
        loadProfileData()
    }

    private fun checkAndSyncUser() {
        val firebaseUser = auth.currentUser ?: return
        viewModelScope.launch {
            // Đảm bảo user tồn tại trong database local
            repository.insertOrUpdateUser(
                UserEntity(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "Người dùng",
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
            )
        }
    }

    private fun loadProfileData() {
        if (currentUserId.isEmpty()) return

        // Theo dõi thông tin User
        viewModelScope.launch {
            repository.getUserProfile(currentUserId).collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        // Theo dõi danh sách phim yêu thích và tính toán thống kê
        viewModelScope.launch {
            repository.getAllFavorites(currentUserId).collect { favorites ->
                val watchedMovies = favorites.filter { it.isWatched }
                
                // 1. Tính tổng thời gian đã xem (phút)
                val totalTime = watchedMovies.sumOf { it.runtime }
                
                // 2. Phân tích thể loại từ danh sách phim đã xem
                val genreCounts = watchedMovies
                    .flatMap { it.genre.split(",").map { g -> g.trim() } }
                    .filter { it.isNotEmpty() }
                    .groupingBy { it }
                    .eachCount()

                _uiState.update { 
                    it.copy(
                        favoriteMovies = favorites,
                        totalWatchTime = totalTime,
                        genreDistribution = genreCounts
                    )
                }
            }
        }
    }

    // Cập nhật tên tài khoản
    fun updateName(newName: String) {
        val user = _uiState.value.user ?: return
        if (newName.isBlank()) return

        viewModelScope.launch {
            // 1. Cập nhật Firebase Auth
            try {
                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(newName).build()
                )?.await()
            } catch (e: Exception) {
                // Log lỗi nếu cần
            }

            // 2. Cập nhật Database Local
            repository.insertOrUpdateUser(user.copy(name = newName))
        }
    }

    // Cập nhật ảnh đại diện
    fun updateAvatar(url: String) {
        val user = _uiState.value.user ?: return
        viewModelScope.launch {
            // 1. Cập nhật Firebase Auth để đồng bộ đám mây
            try {
                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(url))
                        .build()
                )?.await()
            } catch (e: Exception) {
                // Log lỗi nếu cần
            }

            // 2. Cập nhật Database Local
            repository.insertOrUpdateUser(user.copy(avatarUrl = url))
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
}
