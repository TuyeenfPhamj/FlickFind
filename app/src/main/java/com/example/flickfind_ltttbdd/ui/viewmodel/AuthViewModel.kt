package com.example.flickfind_ltttbdd.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickfind_ltttbdd.data.MovieRepository
import com.example.flickfind_ltttbdd.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
)

class AuthViewModel(private val repository: MovieRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { fbAuth: FirebaseAuth ->
        val user = fbAuth.currentUser
        _uiState.update { it.copy(isLoggedIn = user != null) }
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập Email") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mật khẩu") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                
                // Đồng bộ thông tin user vào Room Database ngay khi đăng nhập
                if (user != null) {
                    repository.insertOrUpdateUser(
                        UserEntity(
                            id = user.uid,
                            name = user.displayName ?: "Người dùng",
                            avatarUrl = user.photoUrl?.toString() ?: ""
                        )
                    )
                }

                _uiState.update { it.copy(isLoading = false, isSuccess = true, isLoggedIn = true) }
            } catch (e: Exception) {
                val friendlyMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Tài khoản hoặc mật khẩu không chính xác"
                    else -> "Lỗi đăng nhập: ${translateError(e.message)}"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPass: String) {
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập họ tên") }
            return
        }
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập Email") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Định dạng Email không hợp lệ") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mật khẩu") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu phải có ít nhất 6 ký tự") }
            return
        }
        if (password != confirmPass) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                
                // Cập nhật tên hiển thị
                user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                )?.await()

                // Đồng bộ thông tin user vào Room Database ngay khi đăng ký
                if (user != null) {
                    repository.insertOrUpdateUser(
                        UserEntity(
                            id = user.uid,
                            name = name,
                            avatarUrl = ""
                        )
                    )
                }

                _uiState.update { it.copy(isLoading = false, isSuccess = true, isLoggedIn = true) }
            } catch (e: Exception) {
                val friendlyMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu"
                    is FirebaseAuthInvalidCredentialsException -> "Email không hợp lệ"
                    is FirebaseAuthUserCollisionException -> "Email này đã được sử dụng bởi một tài khoản khác"
                    else -> "Lỗi đăng ký: ${translateError(e.message)}"
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
            }
        }
    }

    private fun translateError(message: String?): String {
        if (message == null) return "Lỗi không xác định"
        return when {
            message.contains("network", ignoreCase = true) -> "Lỗi kết nối mạng"
            message.contains("too many requests", ignoreCase = true) -> "Quá nhiều yêu cầu. Vui lòng thử lại sau"
            else -> message
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(isLoggedIn = false) }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
