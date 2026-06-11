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
import kotlinx.coroutines.delay
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
    val isLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null,
    val isOtpSent: Boolean = false, // Trạng thái đã gửi mã OTP (giả lập)
    val generatedOtp: String = ""    // Mã OTP đã tạo (giả lập)
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
        val parts = email.trim().split("@")
        val cleanEmail = if (parts.size == 2) {
            "${parts[0]}@${parts[1].lowercase()}"
        } else {
            email.trim().lowercase()
        }

        if (cleanEmail.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ thông tin") }
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(errorMessage = "Email không hợp lệ") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.signInWithEmailAndPassword(cleanEmail, password).await()
                val user = result.user
                
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
                    else -> translateError(e.message)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
            }
        }
    }

    // [GHI CHÚ]: Hàm giả lập gửi OTP về Email
    fun sendOtp(email: String) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập Email hợp lệ để nhận mã") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // Giả lập độ trễ gửi mail
            
            // Tạo mã 6 số ngẫu nhiên
            val otp = (100000..999999).random().toString()
            
            _uiState.update { it.copy(
                isLoading = false,
                isOtpSent = true,
                generatedOtp = otp,
                errorMessage = "Mã OTP đã được gửi (Giả lập: $otp)" // Hiển thị mã để test
            ) }
        }
    }

    fun registerWithOtp(name: String, email: String, password: String, otpInput: String) {
        if (otpInput != _uiState.value.generatedOtp) {
            _uiState.update { it.copy(errorMessage = "Mã OTP không chính xác") }
            return
        }

        val parts = email.trim().split("@")
        val cleanEmail = "${parts[0]}@${parts[1].lowercase()}"
        val cleanName = name.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
                val user = result.user
                
                user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(cleanName)
                        .build()
                )?.await()

                if (user != null) {
                    repository.insertOrUpdateUser(
                        UserEntity(
                            id = user.uid,
                            name = cleanName,
                            avatarUrl = ""
                        )
                    )
                }

                _uiState.update { it.copy(
                    isLoading = false, 
                    isSuccess = true, 
                    isLoggedIn = true,
                    isOtpSent = false 
                ) }
            } catch (e: Exception) {
                val friendlyMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu"
                    is FirebaseAuthUserCollisionException -> "Email này đã được sử dụng bởi một tài khoản khác"
                    else -> translateError(e.message)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
            }
        }
    }

    private fun translateError(message: String?): String {
        if (message == null) return "Lỗi không xác định"
        return when {
            message.contains("badly formatted", ignoreCase = true) -> "Email không hợp lệ"
            message.contains("invalid email", ignoreCase = true) -> "Email không hợp lệ"
            message.contains("network", ignoreCase = true) -> "Lỗi kết nối mạng"
            message.contains("too many requests", ignoreCase = true) -> "Quá nhiều yêu cầu. Vui lòng thử lại sau"
            message.contains("user not found", ignoreCase = true) -> "Tài khoản không tồn tại"
            message.contains("wrong password", ignoreCase = true) -> "Mật khẩu không chính xác"
            else -> "Lỗi hệ thống: $message"
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(isLoggedIn = false, isOtpSent = false) }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun cancelOtp() {
        _uiState.update { it.copy(isOtpSent = false, errorMessage = null) }
    }
}
