package com.example.flickfind_ltttbdd.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: String, // Đồng bộ kiểu String với API
    val title: String,
    val posterPath: String,  // Đồng bộ tên biến với API cho dễ quản lý
    val backdropPath: String,
    val genre: String,       // Lưu dưới dạng chuỗi gộp (VD: "Hành động, Viễn tưởng") để vẽ biểu đồ dễ dàng
    val rating: Float,
    val runtime: Int,        // Đồng bộ đổi tên duration thành runtime giống API
    val isWatched: Boolean = false
)