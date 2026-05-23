//package com.example.flickfind_ltttbdd.ui.screens
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.flickfind_ltttbdd.R
//import com.example.flickfind_ltttbdd.ui.viewmodel.AuthViewModel
//
//@Composable
//fun LoginScreen(
//    viewModel: AuthViewModel,
//    onNavigateToRegister: () -> Unit,
//    onLoginSuccess: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    LaunchedEffect(uiState.isSuccess) {
//        if (uiState.isSuccess) {
//            onLoginSuccess()
//            viewModel.resetSuccess()
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF0B101B)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Hiển thị Logo
//            Image(
//                painter = painterResource(id = R.drawable.logo_v1),
//                contentDescription = "Logo",
//                modifier = Modifier.size(150.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Login Card
//            Card(
//                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033).copy(alpha = 0.8f)),
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Đăng nhập",
//                        color = Color.White,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        placeholder = { Text("Email (Tên đăng nhập)", color = Color.Gray) },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    OutlinedTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        placeholder = { Text("Mật khẩu", color = Color.Gray) },
//                        visualTransformation = PasswordVisualTransformation(),
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    Text(
//                        text = "Bạn chưa có tài khoản? Đăng ký ngay!",
//                        color = Color(0xFF00BFA5),
//                        fontSize = 14.sp,
//                        modifier = Modifier.clickable { onNavigateToRegister() }
//                    )
//
//                    Spacer(modifier = Modifier.height(32.dp))
//
//                    if (uiState.isLoading) {
//                        CircularProgressIndicator(color = Color(0xFF38B6FF))
//                    } else {
//                        Button(
//                            onClick = { viewModel.login(email, password) },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23304B)),
//                            modifier = Modifier.fillMaxWidth().height(50.dp),
//                            shape = RoundedCornerShape(8.dp)
//                        ) {
//                            Text("Đăng nhập", color = Color.White, fontSize = 16.sp)
//                        }
//                    }
//
//                    uiState.errorMessage?.let {
//                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun RegisterScreen(
//    viewModel: AuthViewModel,
//    onNavigateToLogin: () -> Unit,
//    onRegisterSuccess: () -> Unit
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    LaunchedEffect(uiState.isSuccess) {
//        if (uiState.isSuccess) {
//            onRegisterSuccess()
//            viewModel.resetSuccess()
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF0B101B)),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Logo
//            Image(
//                painter = painterResource(id = R.drawable.logo_v1),
//                contentDescription = "Logo",
//                modifier = Modifier.size(150.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Register Card
//            Card(
//                colors = CardDefaults.cardColors(containerColor = Color(0xFF172033).copy(alpha = 0.8f)),
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(24.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Đăng ký",
//                        color = Color.White,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    OutlinedTextField(
//                        value = name,
//                        onValueChange = { name = it },
//                        placeholder = { Text("Tên tài khoản", color = Color.Gray) },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = { email = it },
//                        placeholder = { Text("Email (Tên đăng nhập)", color = Color.Gray) },
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    OutlinedTextField(
//                        value = password,
//                        onValueChange = { password = it },
//                        placeholder = { Text("Mật khẩu", color = Color.Gray) },
//                        visualTransformation = PasswordVisualTransformation(),
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    OutlinedTextField(
//                        value = confirmPassword,
//                        onValueChange = { confirmPassword = it },
//                        placeholder = { Text("Nhập lại mật khẩu", color = Color.Gray) },
//                        visualTransformation = PasswordVisualTransformation(),
//                        modifier = Modifier.fillMaxWidth(),
//                        colors = OutlinedTextFieldDefaults.colors(
//                            focusedTextColor = Color.White,
//                            unfocusedTextColor = Color.White,
//                            focusedBorderColor = Color(0xFF38B6FF),
//                            unfocusedBorderColor = Color(0xFF233044),
//                            focusedContainerColor = Color(0xFF131C2E),
//                            unfocusedContainerColor = Color(0xFF131C2E)
//                        )
//                    )
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    Text(
//                        text = "Nhấn để đăng nhập!",
//                        color = Color(0xFF00BFA5),
//                        fontSize = 14.sp,
//                        modifier = Modifier.clickable { onNavigateToLogin() }
//                    )
//
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    if (uiState.isLoading) {
//                        CircularProgressIndicator(color = Color(0xFF38B6FF))
//                    } else {
//                        Button(
//                            onClick = { viewModel.register(name, email, password, confirmPassword) },
//                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23304B)),
//                            modifier = Modifier.fillMaxWidth().height(50.dp),
//                            shape = RoundedCornerShape(8.dp)
//                        ) {
//                            Text("Đăng ký", color = Color.White, fontSize = 16.sp)
//                        }
//                    }
//
//                    uiState.errorMessage?.let {
//                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
//                    }
//                }
//            }
//        }
//    }
//}
