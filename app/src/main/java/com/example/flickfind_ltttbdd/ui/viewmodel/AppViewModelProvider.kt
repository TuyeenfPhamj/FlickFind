package com.example.flickfind_ltttbdd.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.AppDatabase
import com.example.flickfind_ltttbdd.data.remote.RetrofitClient

/**
 * Cung cấp Factory để khởi tạo các ViewModel với đúng tham số (Repository)
 */
class AppViewModelProvider(private val context: Context) : ViewModelProvider.Factory {
    
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)
        val repository = MovieRepository(
            apiService = RetrofitClient.instance,
            movieDao = database.movieDao(),
            userDao = database.userDao()
        )
        
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
