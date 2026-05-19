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
    val movies: List<MovieResponse> = emptyList(), // Danh sách phim đã tải được gộp lại
    val favoriteMovieIds: Set<Int> = emptySet(),   // Danh sách ID phim đã thích để đổi màu Icon Trái tim
    val isLoading: Boolean = false,                // Đang tải dữ liệu trang đầu hoặc trang tiếp theo
    val errorMessage: String? = null,              // Thông báo lỗi nếu mất mạng/lỗi API
    val currentPage: Int = 1,                      // Trang hiện tại
    val isEndReached: Boolean = false              // Đã tải hết sạch 60 phim chưa
)

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    // Khai báo StateFlow nội bộ và mã hóa đầu ra chỉ đọc cho UI
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val PAGE_LIMIT = 10 // Mỗi lần cuộn sẽ tải thêm 10 bộ phim

    init {
        // Tự động tải trang đầu tiên khi ứng dụng mở lên
        loadNextMovies()
        // Lắng nghe danh sách phim yêu thích từ Room để đồng bộ Icon Trái tim trên UI
        observeFavorites()
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