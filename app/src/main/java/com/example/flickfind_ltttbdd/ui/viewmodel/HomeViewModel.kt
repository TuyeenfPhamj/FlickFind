package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Định nghĩa trạng thái giao diện (UI State) theo chuẩn UDF
data class HomeUiState(
    val movies: List<MovieResponse> = emptyList(), // Danh sách phim cho trang chủ (phân trang)
    val popularMovies: List<MovieResponse> = emptyList(), // Danh sách 10 phim phổ biến (điểm cao)
    val favoriteMovieIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false,
    val searchSuggestions: List<MovieResponse> = emptyList() // Danh sách gợi ý tìm kiếm
)

class HomeViewModel(
    private val repository: MovieRepository,
    private val userId: String = "guest_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val pageLimit = 10
    private var allMoviesForSuggestions: List<MovieResponse> = emptyList()

    init {
        loadNextMovies()
        observeFavorites()
        fetchAllMoviesForSuggestions()
    }

    private fun fetchAllMoviesForSuggestions() {
        viewModelScope.launch {
            // Tải 100 phim một lần để phục vụ gợi ý tìm kiếm tức thì và lấy phim phổ biến
            repository.getMoviesFromApi(page = 1, limit = 100).onSuccess { all ->
                allMoviesForSuggestions = all
                // Lấy 10 phim có rating cao nhất làm phim phổ biến
                val popular = all.sortedByDescending { it.rating }.take(10)
                _uiState.update { it.copy(popularMovies = popular) }
            }
        }
    }

    fun updateSearchSuggestions(query: String) {
        if (query.length <= 3) {
            _uiState.update { it.copy(searchSuggestions = emptyList()) }
            return
        }
        
        val filtered = allMoviesForSuggestions.filter { 
            it.title.contains(query, ignoreCase = true) 
        }.take(5)
        
        _uiState.update { it.copy(searchSuggestions = filtered) }
    }

    fun clearSuggestions() {
        _uiState.update { it.copy(searchSuggestions = emptyList()) }
    }

    // Làm mới toàn bộ danh sách
    fun refreshMovies() {
        _uiState.update { it.copy(
            currentPage = 1,
            movies = emptyList(),
            isEndReached = false,
            isLoading = true,
            errorMessage = null
        ) }
        loadNextMovies()
        fetchAllMoviesForSuggestions()
    }

    // 2. Logic Phân trang (Pagination) thủ công cực kỳ trực quan
    fun loadNextMovies() {
        // Nếu đang tải hoặc đã hết phim thì không gọi API nữa
        if (_uiState.value.isLoading && _uiState.value.currentPage > 1 || _uiState.value.isEndReached) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.getMoviesFromApi(
                page = _uiState.value.currentPage,
                limit = pageLimit
            )

            result.onSuccess { newMovies ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        movies = if (currentState.currentPage == 1) newMovies else currentState.movies + newMovies,
                        currentPage = currentState.currentPage + 1,
                        isEndReached = newMovies.size < pageLimit
                    )
                }
            }.onFailure { exception ->
                val friendlyError = if (exception is java.net.UnknownHostException || exception.message?.contains("Unable to resolve host") == true) {
                    "Không có kết nối mạng, vui lòng thử lại"
                } else {
                    exception.localizedMessage ?: "Lỗi kết nối API"
                }
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = friendlyError)
                }
            }
        }
    }

    // 3. Theo dõi danh sách phim đã lưu trong Room DB theo userId
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites(userId).collect { favoriteEntities ->
                _uiState.update { currentState ->
                    // Chuyển danh sách thực thể thành một bộ Set<Int> chứa ID để tìm kiếm siêu nhanh (O(1))
                    currentState.copy(favoriteMovieIds = favoriteEntities.map { it.id }.toSet())
                }
            }
        }
    }

    // 4. Tính năng Click vào nút "Thích" (CRUD - Thêm/Xóa khỏi Room DB trực tiếp từ danh sách)
    fun toggleFavorite(movie: MovieResponse) {
        viewModelScope.launch {
            val isFav = repository.isMovieFavorite(movie.id, userId)
            // Chuyển đổi dữ liệu từ dạng API Response sang thực thể Room DB
            val entity = FavoriteMovieEntity(
                id = movie.id,
                userId = userId,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                genre = movie.genres.joinToString(", "), // Gộp mảng List<String> thành một chuỗi String đơn
                rating = movie.rating,
                runtime = movie.runtime,
                isWatched = false // Mặc định khi ấn thích từ màn chính là chưa xem
            )

            if (isFav) {
                repository.removeFromFavorite(entity)
            } else {
                repository.addToFavorite(entity)
            }
        }
    }
}
