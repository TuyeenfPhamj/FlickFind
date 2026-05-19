package com.example.flickfind_ltttbdd.data

import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.data.local.MovieDao
import com.example.flickfind_ltttbdd.data.local.UserDao
import com.example.flickfind_ltttbdd.data.local.UserEntity
import com.example.flickfind_ltttbdd.data.remote.MovieApiService
import com.example.flickfind_ltttbdd.data.remote.MovieResponse
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao,
    private val userDao: UserDao
) {

    suspend fun getMoviesFromApi(page: Int, limit: Int): Result<List<MovieResponse>> {
        return try {
            val response = apiService.getMovies(page, limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>> = movieDao.getAllFavorites()

    suspend fun addToFavorite(movie: FavoriteMovieEntity) {
        movieDao.insertFavorite(movie)
    }

    suspend fun removeFromFavorite(movie: FavoriteMovieEntity) {
        movieDao.deleteFavorite(movie)
    }

    suspend fun isMovieFavorite(movieId: Int): Boolean {
        return movieDao.getMovieById(movieId) != null
    }

    fun getUserProfile(): Flow<UserEntity?> = userDao.getUserProfile()

    suspend fun updateUserProfile(user: UserEntity) {
        userDao.insertOrUpdateUser(user)
    }
}
