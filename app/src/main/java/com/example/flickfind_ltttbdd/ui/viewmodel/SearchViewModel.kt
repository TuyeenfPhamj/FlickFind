package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val movies: List<MovieResponse> = emptyList(),
    val favoriteMovieIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val query: String? = null,
    val genre: String? = null,
    val yearRange: String? = null,
    val sortBy: String? = null,
    val isEndReached: Boolean = false
)

class SearchViewModel(
    private val repository: MovieRepository,
    private val userId: String = "guest_user"
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentSearchJob: Job? = null

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getAllFavorites(userId).collect { favoriteEntities ->
                _uiState.update { currentState ->
                    currentState.copy(favoriteMovieIds = favoriteEntities.map { it.id }.toSet())
                }
            }
        }
    }

    fun setFiltersAndSearch(query: String?, genre: String?, yearRange: String?, sortBy: String? = null) {
        // Chuẩn hóa: Nếu chuỗi rỗng hoặc chỉ có khoảng trắng thì coi như null
        val cleanQuery = query?.takeIf { it.isNotBlank() }
        val cleanGenre = genre?.takeIf { it.isNotBlank() }
        val cleanYear = yearRange?.takeIf { it.isNotBlank() }
        val cleanSort = sortBy?.takeIf { it.isNotBlank() }

        _uiState.update { 
            it.copy(
                query = cleanQuery, 
                genre = cleanGenre, 
                yearRange = cleanYear,
                sortBy = cleanSort,
                movies = emptyList(),
                isEndReached = false,
                isLoading = false,
                errorMessage = null
            )
        }
        
        // Hủy job tìm kiếm cũ nếu người dùng thay đổi bộ lọc liên tục
        currentSearchJob?.cancel()
        currentSearchJob = viewModelScope.launch {
            performSearch()
        }
    }

    private suspend fun performSearch() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        // Giải pháp triệt để: Tải toàn bộ danh sách phim (100 phim) và lọc local.
        // Điều này đảm bảo bộ lọc hoạt động độc lập, không phụ thuộc vào trạng thái 
        // cuộn của màn hình chính và khắc phục giới hạn lọc của MockAPI.
        val result = repository.getMoviesFromApi(
            page = 1,
            limit = 100, // Tải đủ số lượng phim dự kiến trong hệ thống
            search = null
        )

        result.onSuccess { allMovies ->
            // ... (keeping existing logic for filtering)
            val filtered = allMovies.filter { movie ->
                val q = _uiState.value.query
                val g = _uiState.value.genre
                val y = _uiState.value.yearRange

                // 1. Lọc theo từ khóa (Tiêu đề)
                val matchQuery = q == null || movie.title.contains(q, ignoreCase = true)
                
                // 2. Lọc theo Thể loại (Lọc trong mảng genres)
                val matchGenre = g == null || movie.genres.any { it.contains(g, ignoreCase = true) }
                
                // 3. Lọc theo Khoảng năm (Dựa trên releaseDate YYYY-MM-DD)
                val matchYear = y == null || matchesYearRange(movie.releaseDate, y)

                matchQuery && matchGenre && matchYear
            }

            // Sắp xếp nếu có yêu cầu
            val sorted = if (_uiState.value.sortBy == "rating") {
                filtered.sortedByDescending { it.rating }
            } else {
                filtered
            }

            _uiState.update { it.copy(
                isLoading = false, 
                movies = sorted,
                isEndReached = true // Đã hoàn thành tải và lọc toàn bộ kho phim
            ) }
        }.onFailure { e ->
            val friendlyError = if (e is java.net.UnknownHostException || e.message?.contains("Unable to resolve host") == true) {
                "Không có kết nối mạng, vui lòng thử lại"
            } else {
                e.localizedMessage ?: "Không có kết nối mạng, vui lòng thử lại"
            }
            _uiState.update { it.copy(
                isLoading = false, 
                errorMessage = friendlyError
            ) }
        }
    }

    private fun matchesYearRange(releaseDate: String, range: String): Boolean {
        return try {
            // range: "2021 - 2025" -> start=2021, end=2025
            val years = range.split("-").map { it.trim().toIntOrNull() }
            val start = years.getOrNull(0) ?: 0
            val end = years.getOrNull(1) ?: 9999
            
            // Lấy 4 ký tự đầu của "YYYY-MM-DD"
            val movieYear = releaseDate.take(4).toIntOrNull() ?: 0
            movieYear in start..end
        } catch (e: Exception) {
            true // Nếu lỗi định dạng thì bỏ qua lọc năm này
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun loadNextMovies() {
        // Không cần loadNext nữa vì performSearch đã tải và lọc toàn bộ kho phim ngay lần đầu
    }

    fun toggleFavorite(movie: MovieResponse) {
        viewModelScope.launch {
            val isFav = repository.isMovieFavorite(movie.id, userId)
            val entity = FavoriteMovieEntity(
                id = movie.id,
                userId = userId,
                title = movie.title,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                genre = movie.genres.joinToString(", "),
                rating = movie.rating,
                runtime = movie.runtime,
                isWatched = false
            )

            if (isFav) {
                repository.removeFromFavorite(entity)
            } else {
                repository.addToFavorite(entity)
            }
        }
    }
}
