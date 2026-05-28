package com.example.flickfind_ltttbdd.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flickfind_ltttbdd.data.local.FavoriteMovieEntity
import com.example.flickfind_ltttbdd.ui.viewmodel.ProfileViewModel
import com.example.flickfind_ltttbdd.navigation.Screen
import com.example.flickfind_ltttbdd.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController,
    isDarkTheme: Boolean,
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // [GHI CHÚ]: Đồng bộ màu sắc theo theme Sáng/Tối
    val bgColor = if (isDarkTheme) Color(0xFF0B101B) else Color(0xFFF0F4F8)
    val cardColor = if (isDarkTheme) Color(0xFF172033) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray
    val secondaryCardColor = if (isDarkTheme) Color(0xFF23304B) else Color(0xFFDEE5EF)

    val context = LocalContext.current
    var isEditMode by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { 
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {}
                viewModel.updateAvatar(it.toString()) 
            }
        }
    )

    if (showNameDialog) {
        NameEditDialog(
            currentName = uiState.user?.name ?: "",
            cardColor = cardColor,
            textColor = textColor,
            onDismiss = { showNameDialog = false },
            onConfirm = { newName ->
                viewModel.updateName(newName)
                showNameDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeader(
                name = uiState.user?.name ?: "Người dùng",
                avatarUrl = uiState.user?.avatarUrl ?: "",
                cardColor = cardColor,
                textColor = textColor,
                secondaryColor = secondaryCardColor,
                onLogout = onLogout,
                onEditAvatar = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onEditName = { showNameDialog = true }
            )
        }

        item {
            Text(
                text = "Bảng thống kê thể loại phim đã xem",
                color = if (isDarkTheme) Color.Cyan else Color(0xFF00796B),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            GenreBarChartSection(
                genreDistribution = uiState.genreDistribution,
                totalTime = uiState.totalWatchTime,
                cardColor = cardColor,
                textColor = textColor,
                secondaryColor = secondaryCardColor,
                isDarkTheme = isDarkTheme
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách yêu thích",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    onClick = { isEditMode = !isEditMode },
                    color = if (isEditMode) Color(0xFFE91E63) else cardColor,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEditMode) "Xong" else "Sửa",
                            color = if (isEditMode || isDarkTheme) Color.White else Color.Black,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (isEditMode || isDarkTheme) Color.White else Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        items(uiState.favoriteMovies) { movie ->
            HorizontalFavoriteMovieItem(
                movie = movie,
                isEditMode = isEditMode,
                cardColor = cardColor,
                textColor = textColor,
                subTextColor = subTextColor,
                onToggleWatched = { viewModel.toggleWatched(movie) },
                onDelete = { viewModel.deleteFavorite(movie) },
                onClick = {
                    if (!isEditMode) {
                        navController.navigate(Screen.Detail.createRoute(movie.id))
                    }
                }
            )
        }
    }
}

@Composable
fun HorizontalFavoriteMovieItem(
    movie: FavoriteMovieEntity,
    isEditMode: Boolean,
    cardColor: Color,
    textColor: Color,
    subTextColor: Color,
    onToggleWatched: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = if (movie.posterPath.startsWith("/")) {
                "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            } else {
                movie.posterPath
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.genre,
                    color = subTextColor,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = movie.rating.toString(), color = textColor, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${movie.runtime}p", color = subTextColor, fontSize = 12.sp)
                }

                if (isEditMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusChoiceChip(
                            text = "Đã xem",
                            isSelected = movie.isWatched,
                            onClick = { if (!movie.isWatched) onToggleWatched() }
                        )
                        StatusChoiceChip(
                            text = "Chưa xem",
                            isSelected = !movie.isWatched,
                            onClick = { if (movie.isWatched) onToggleWatched() }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (isEditMode) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Surface(
                        color = if (movie.isWatched) Color(0xFF00BFA5) else Color(0xFFE91E63),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 60.dp)
                    ) {
                        Text(
                            text = if (movie.isWatched) "Đã xem" else "Chưa xem",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChoiceChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.Cyan.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isSelected) Color.Cyan else Color.Gray.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Cyan else Color.Gray,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun GenreBarChartSection(genreDistribution: Map<String, Int>, totalTime: Int, cardColor: Color, textColor: Color, secondaryColor: Color, isDarkTheme: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Phân bổ thể loại", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = "Tổng: $totalTime phút", color = if (isDarkTheme) Color.Cyan else Color(0xFF00796B), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (genreDistribution.isEmpty()) {
                Text("Chưa có dữ liệu", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                val maxCount = genreDistribution.values.maxOrNull()?.toFloat() ?: 1f
                genreDistribution.entries.sortedByDescending { it.value }.take(4).forEach { (genre, count) ->
                    GenreBarItem(genre, count, count / maxCount, textColor, secondaryColor, isDarkTheme)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GenreBarItem(genre: String, count: Int, ratio: Float, textColor: Color, secondaryColor: Color, isDarkTheme: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = genre, color = textColor.copy(alpha = 0.7f), fontSize = 10.sp)
            Text(text = "$count phim", color = textColor, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(secondaryColor)) {
            Box(modifier = Modifier.fillMaxWidth(ratio).fillMaxHeight().clip(CircleShape).background(if (isDarkTheme) Color.Cyan else Color(0xFF009688)))
        }
    }
}

@Composable
fun ProfileHeader(name: String, avatarUrl: String, cardColor: Color, textColor: Color, secondaryColor: Color, onLogout: () -> Unit, onEditAvatar: () -> Unit, onEditName: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(secondaryColor)
                    .clickable { onEditAvatar() }, 
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNotEmpty()) {
                    AsyncImage(model = avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = name, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa tên",
                        tint = Color.Cyan,
                        modifier = Modifier.size(14.dp).clickable { onEditName() }
                    )
                }
                Text(text = "ID: ${FirebaseAuth.getInstance().currentUser?.uid?.take(8)}...", color = Color.Gray, fontSize = 10.sp)
            }
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Đăng xuất", color = textColor, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun NameEditDialog(currentName: String, cardColor: Color, textColor: Color, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi tên tài khoản", color = textColor) },
        containerColor = cardColor,
        text = {
            Column {
                Text("Nhập tên mới:", color = textColor.copy(alpha = 0.7f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = Color.Cyan,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Cập nhật", color = Color.Cyan)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Hủy", color = Color.Gray)
            }
        }
    )
}
