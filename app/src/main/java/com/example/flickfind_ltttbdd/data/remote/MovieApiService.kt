package com.example.flickfind_ltttbdd.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    // Hàm gọi API lấy danh sách phim có áp dụng phân trang và bộ lọc (Tìm kiếm, Thể loại, Năm)
    @GET("api/v1/movies")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String? = null,
        @Query("genre") genre: String? = null,
        @Query("yearRange") yearRange: String? = null
    ): List<MovieResponse>

    // Lấy chi tiết một bộ phim theo ID
    @GET("api/v1/movies/{id}")
    suspend fun getMovieById(
        @retrofit2.http.Path("id") id: Int
    ): MovieResponse
}