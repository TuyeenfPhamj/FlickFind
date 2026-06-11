package com.example.flickfind_ltttbdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flickfind_ltttbdd.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onNavigateToRegister: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("FlickFind", color = colorScheme.primary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant,
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.outline
            ),
            singleLine = true
        )
        
        uiState.errorMessage?.let {
            Text(it, color = colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            shape = MaterialTheme.shapes.medium
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Đăng nhập", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Chưa có tài khoản? ", color = colorScheme.onSurfaceVariant)
            TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                Text("Đăng ký ngay", color = colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(
                message = "Đăng ký thành công!",
                duration = SnackbarDuration.Short
            )
            delay(1500)
            viewModel.resetSuccess()
            onNavigateToLogin()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!uiState.isOtpSent) {
                // MÀN HÌNH NHẬP THÔNG TIN ĐĂNG KÝ
                Text("Tạo tài khoản", color = colorScheme.primary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ tên") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline
                    ),
                    singleLine = true
                )
                
                uiState.errorMessage?.let {
                    Text(it, color = colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        if (password == confirmPassword) {
                            viewModel.sendOtp(email) 
                        } else {
                            // Sẽ được ViewModel xử lý nhưng ta check nhanh ở đây
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Tiếp tục", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text("Đã có tài khoản? Đăng nhập", color = colorScheme.onSurfaceVariant)
                }
            } else {
                // MÀN HÌNH NHẬP MÃ OTP
                Text("Xác thực Email", color = colorScheme.primary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Chúng tôi đã gửi mã xác thực đến $email. Vui lòng nhập mã để hoàn tất.",
                    color = colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = otpInput,
                    onValueChange = { if (it.length <= 6) otpInput = it },
                    label = { Text("Nhập mã OTP (6 số)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onSurface,
                        unfocusedTextColor = colorScheme.onSurface,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedLabelColor = colorScheme.onSurfaceVariant,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.outline
                    ),
                    singleLine = true
                )

                uiState.errorMessage?.let {
                    Text(it, color = colorScheme.error, modifier = Modifier.padding(top = 8.dp), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.registerWithOtp(name, email, password, otpInput) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Xác nhận đăng ký", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = { viewModel.cancelOtp() }) {
                    Text("Quay lại", color = colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
