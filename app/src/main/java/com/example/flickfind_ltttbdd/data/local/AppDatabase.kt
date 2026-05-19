package com.example.flickfind_ltttbdd.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FavoriteMovieEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Khai báo các đường dẫn để lấy DAO
    abstract fun movieDao(): MovieDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Hàm khởi tạo Database dạng Singleton (đảm bảo toàn app chỉ có 1 thực thể DB độc nhất)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flick_find_database" // Tên file database lưu trên điện thoại
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Chèn dữ liệu mẫu khi database được tạo lần đầu
                            CoroutineScope(Dispatchers.IO).launch {
                                // Lấy instance vừa tạo để seed data
                                getDatabase(context).let { database ->
                                    seedDatabase(database.userDao(), database.movieDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(userDao: UserDao, movieDao: MovieDao) {
            // 1. Tạo User mẫu
            userDao.insertOrUpdateUser(
                UserEntity(id = 1, name = "Nguyễn Văn A", avatarUrl = "https://i.pravatar.cc/150?u=flickfind")
            )

            // 2. Tạo danh sách phim mẫu
            val mockMovies = listOf(
                FavoriteMovieEntity(
                    id = 101,
                    title = "Inception",
                    posterPath = "/edv5bs1pUQC67SWHqcYf67OQ97R.jpg",
                    backdropPath = "",
                    genre = "Hành động, Khoa học viễn tưởng",
                    rating = 8.8f,
                    runtime = 148,
                    isWatched = true
                ),
                FavoriteMovieEntity(
                    id = 102,
                    title = "The Dark Knight",
                    posterPath = "/qJ2tW6WMUDp9QmSJJIVP6YFZO8r.jpg",
                    backdropPath = "",
                    genre = "Hành động, Hình sự",
                    rating = 9.0f,
                    runtime = 152,
                    isWatched = true
                ),
                FavoriteMovieEntity(
                    id = 103,
                    title = "Interstellar",
                    posterPath = "/gEU2QniE6E77NI6lCU6MxlS67jP.jpg",
                    backdropPath = "",
                    genre = "Khoa học viễn tưởng, Phiêu lưu",
                    rating = 8.7f,
                    runtime = 169,
                    isWatched = false
                )
            )
            
            mockMovies.forEach { movieDao.insertFavorite(it) }
        }
    }
}
