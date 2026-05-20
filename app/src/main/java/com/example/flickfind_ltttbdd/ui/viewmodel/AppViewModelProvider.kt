package com.example.flickfind_ltttbdd.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.AppDatabase
import com.example.flickfind_ltttbdd.data.remote.RetrofitClient

class AppViewModelProvider(
    private val context: Context,
    private val movieId: Int? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)
        val repository = MovieRepository(
            apiService = RetrofitClient.instance,
            movieDao = database.movieDao(),
            userDao = database.userDao(),
        )

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                DetailViewModel(repository, movieId ?: 0) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
