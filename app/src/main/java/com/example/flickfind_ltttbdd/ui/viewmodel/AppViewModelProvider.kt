package com.example.flickfind_ltttbdd.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.AppDatabase
import com.example.flickfind_ltttbdd.data.remote.RetrofitClient
import kotlin.jvm.java

class AppViewModelProvider(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)
        val repository = MovieRepository(
            apiService = RetrofitClient.instance,
            movieDao = database.movieDao(),
            userDao = database.userDao()
        )

//        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return HomeViewModel(repository) as T
//        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}