package com.example.autoglazecustomer.ui.profile.editprofile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import autoglazecustomer.composeapp.generated.resources.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.model.ProfileData
import com.example.autoglazecustomer.data.network.AuthService
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class EditProfileScreen(
    private val authService: AuthService,
    private val initialProfileData: ProfileData?
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { EditProfileScreenModel(authService) }

        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)

        // Sync data awal
        LaunchedEffect(Unit) {
            if (screenModel.nama.isEmpty()) {
                screenModel.nama = initialProfileData?.nama ?: ""
                screenModel.email = initialProfileData?.email ?: ""
                screenModel.telepon = initialProfileData?.telepon ?: ""
                screenModel.currentPhotoUrl = initialProfileData?.photo
            }
        }

        val scope = rememberCoroutineScope()
        val imagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            resizeOptions = ResizeOptions( 
                width = 1000,
                height = 1000,
                compressionQuality = 0.8
            ),
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let { bytes ->
                    screenModel.selectedImageBytes = bytes
                }
            }
        )


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profil", fontFamily = satoshiBold, fontSize = 19.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFFBFBFB))
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- PHOTO SECTION ---
                    Box(contentAlignment = Alignment.BottomEnd) {
                        val displayPhoto: Any? = screenModel.selectedImageBytes ?: screenModel.currentPhotoUrl

                        AsyncImage(
                            model = displayPhoto,
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color(0xFFFFD6D6), CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(Res.drawable.ic_profile_white)
                        )

                        if (screenModel.isEditing) {
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { imagePicker.launch() },
                                shape = CircleShape,
                                color = redPrimer,
                                shadowElevation = 4.dp
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // --- ERROR MESSAGE (Tampil jika ada error) ---
                    if (screenModel.errorMessage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = screenModel.errorMessage!!,
                                color = Color(0xFFC62828),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center,
                                fontFamily = satoshiMedium
                            )
                        }
                    }

                    // --- FORM INPUTS ---
                    EditField("Nama Lengkap", screenModel.nama, screenModel.isEditing, satoshiMedium, Icons.Default.Person) { screenModel.nama = it }
                    EditField("Email", screenModel.email, screenModel.isEditing, satoshiMedium, Icons.Default.Email) { screenModel.email = it }
                    EditField("Nomor Telepon", screenModel.telepon, screenModel.isEditing, satoshiMedium, Icons.Default.Phone) { screenModel.telepon = it }

                    Spacer(Modifier.height(40.dp))

                    // --- BUTTON ACTION ---
                    Button(
                        onClick = {
                            if (screenModel.isEditing) screenModel.updateProfile()
                            else screenModel.isEditing = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (screenModel.isEditing) redPrimer else Color.White
                        ),
                        border = if (!screenModel.isEditing) BorderStroke(1.dp, redPrimer) else null,
                        enabled = !screenModel.isLoading
                    ) {
                        if (screenModel.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            val text = if (screenModel.isEditing) "Simpan Perubahan" else "Edit Profil"
                            val color = if (screenModel.isEditing) Color.White else redPrimer
                            Text(text, color = color, fontFamily = satoshiBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // --- SUCCESS DIALOG ---
        // --- SUCCESS DIALOG (Rata Tengah Profesional) ---
        if (screenModel.showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(64.dp)
                    )
                },
                title = {
                    Text(
                        text = "Berhasil",
                        fontFamily = satoshiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() // Memaksa teks mengambil ruang penuh agar bisa center
                    )
                },
                text = {
                    Text(
                        text = "Profil anda telah diperbarui.",
                        fontFamily = satoshiMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth() // Memaksa teks mengambil ruang penuh agar bisa center
                    )
                },
                confirmButton = {
                    // Membungkus Button dengan Box agar tombol bisa rata tengah di dalam dialog
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                screenModel.showSuccessDialog = false
                                navigator.pop()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = redPrimer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.7f) // Tombol dibuat tidak terlalu lebar agar estetik
                        ) {
                            Text("Oke", fontFamily = satoshiBold, color = Color.White)
                        }
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }

    @Composable
    private fun EditField(
        label: String,
        value: String,
        enabled: Boolean,
        font: FontFamily,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        onValueChange: (String) -> Unit
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text(label, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp), fontFamily = font)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = font),
                leadingIcon = { Icon(icon, contentDescription = null, tint = if (enabled) Color(0xFFD53B1E) else Color.Gray, modifier = Modifier.size(20.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD53B1E),
                    unfocusedBorderColor = Color(0xFFEEEEEE),
                    disabledBorderColor = Color(0xFFF5F5F5),
                    disabledTextColor = Color.Black,
                    focusedLeadingIconColor = Color(0xFFD53B1E)
                ),
                singleLine = true
            )
        }
    }
}