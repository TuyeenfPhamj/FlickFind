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
    val favoriteMovieIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false,
    val searchSuggestions: List<MovieResponse> = emptyList() // Danh sách gợi ý tìm kiếm
)

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val PAGE_LIMIT = 10
    private var allMoviesForSuggestions: List<MovieResponse> = emptyList()

    init {
        loadNextMovies()
        observeFavorites()
        fetchAllMoviesForSuggestions()
    }

    private fun fetchAllMoviesForSuggestions() {
        viewModelScope.launch {
            // Tải 100 phim một lần để phục vụ gợi ý tìm kiếm tức thì
            repository.getMoviesFromApi(page = 1, limit = 100).onSuccess { all ->
                allMoviesForSuggestions = all
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

    // 2. Logic Phân trang (Pagination) thủ công cực kỳ trực quan
    fun loadNextMovies() {
        // Nếu đang tải hoặc đã hết phim thì không gọi API nữa để tiết kiệm băng thông
        if (_uiState.value.isLoading || _uiState.value.isEndReached) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.getMoviesFromApi(
                page = _uiState.value.currentPage,
                limit = PAGE_LIMIT
            )

            result.onSuccess { newMovies ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        // Gộp danh sách phim cũ và phim mới tải về lại thành một danh sách duy nhất
                        movies = currentState.movies + newMovies,
                        // Tăng số trang lên 1 để chuẩn bị cho lần cuộn tiếp theo
                        currentPage = currentState.currentPage + 1,
                        // Nếu API trả về ít hơn giới hạn nghĩa là đã chạm đáy danh sách
                        isEndReached = newMovies.size < PAGE_LIMIT
                    )
                }
            } .onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.localizedMessage ?: "Lỗi kết nối API")
                }
            }
        }
    }

    // 3. Theo dõi danh sách phim đã lưu trong Room DB
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favoriteEntities ->
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
            val isFav = repository.isMovieFavorite(movie.id)
            // Chuyển đổi dữ liệu từ dạng API Response sang thực thể Room DB
            val entity = FavoriteMovieEntity(
                id = movie.id,
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