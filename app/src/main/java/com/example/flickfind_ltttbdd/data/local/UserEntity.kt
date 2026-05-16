package com.example.flickfind_ltttbdd.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1, // Luôn cố định là 1 vì chỉ có 1 user cục bộ
    val name: String,
    val avatarUrl: String // Đường dẫn ảnh đại diện (có thể để ảnh mặc định hoặc link ảnh)
)