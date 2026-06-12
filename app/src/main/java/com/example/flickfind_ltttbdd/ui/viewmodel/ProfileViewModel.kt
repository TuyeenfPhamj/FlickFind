package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Job
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
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var userDataJob: Job? = null
    private var favoritesDataJob: Job? = null

    // [GHI CHÚ]: Lắng nghe sự thay đổi tài khoản. Tự động reset và tải lại dữ liệu mới.
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // [GHI CHÚ]: Reset UI state về rỗng trước khi tải dữ liệu của tài khoản mới
            _uiState.value = ProfileUiState()
            loadProfileData(userId)
        } else {
            // Hủy các kết nối và xóa sạch dữ liệu khi đăng xuất
            userDataJob?.cancel()
            favoritesDataJob?.cancel()
            _uiState.value = ProfileUiState()
        }
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    private fun loadProfileData(userId: String) {
        userDataJob?.cancel()
        favoritesDataJob?.cancel()

        userDataJob = viewModelScope.launch {
            repository.getUserProfile(userId).collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        favoritesDataJob = viewModelScope.launch {
            repository.getAllFavorites(userId).collect { favorites ->
                val watchedMovies = favorites.filter { it.isWatched }
                val totalTime = watchedMovies.sumOf { it.runtime }
                
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

    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    fun toggleWatched(movie: FavoriteMovieEntity) {
        viewModelScope.launch {
            repository.addToFavorite(movie.copy(isWatched = !movie.isWatched))
        }
    }
    
    fun deleteFavorite(movie: FavoriteMovieEntity) {
        viewModelScope.launch {
            repository.removeFromFavorite(movie)
        }
    }

    fun updateAvatar(url: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        viewModelScope.launch {
            try {
                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setPhotoUri(android.net.Uri.parse(url))
                        .build()
                ).await()
                
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
                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                ).await()
                
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
