package com.example.flickfind_ltttbdd.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 1. Lấy thông tin người dùng duy nhất (ID = 1)
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>

    // 2. Cập nhật hoặc khởi tạo thông tin người dùng
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)
}