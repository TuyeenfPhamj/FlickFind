package com.example.flickfind_ltttbdd.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.AppDatabase
import com.example.flickfind_ltttbdd.data.remote.RetrofitClient

class AppViewModelProvider(private val context: Context) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var repository: MovieRepository? = null

        fun getRepository(context: Context): MovieRepository {
            return repository ?: synchronized(this) {
                val database = AppDatabase.getDatabase(context)
                val repo = MovieRepository(
                    apiService = RetrofitClient.instance,
                    movieDao = database.movieDao(),
                    userDao = database.userDao()
                )
                repository = repo
                repo
            }
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = getRepository(context)
        // Trong thực tế, bạn sẽ lấy userId từ FirebaseAuth.
        // Ví dụ: val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest_user"
        val userId = "guest_user" 

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo, userId) as T
        }

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repo, userId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}