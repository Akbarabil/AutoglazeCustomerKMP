package com.example.autoglazecustomer.ui.profile.editprofile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import autoglazecustomer.composeapp.generated.resources.Res
import autoglazecustomer.composeapp.generated.resources.ic_profile_white
import autoglazecustomer.composeapp.generated.resources.satoshi_bold
import autoglazecustomer.composeapp.generated.resources.satoshi_medium
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.example.autoglazecustomer.data.model.ProfileData
import com.example.autoglazecustomer.data.network.AuthService
import com.preat.peekaboo.image.picker.ResizeOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

class EditProfileScreen(
    private val profileDataJson: String?
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<EditProfileScreenModel>()
        val initialProfileData = remember {
            profileDataJson?.let {
                try { Json.decodeFromString<ProfileData>(it) } catch (e: Exception) { null }
            }
        }
        val satoshiBold = FontFamily(Font(Res.font.satoshi_bold, FontWeight.Bold))
        val satoshiMedium = FontFamily(Font(Res.font.satoshi_medium, FontWeight.Medium))
        val redPrimer = Color(0xFFD53B1E)


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
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Edit Profil",
                            fontFamily = satoshiBold,
                            fontSize = 19.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
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

                    Box(contentAlignment = Alignment.BottomEnd) {
                        val displayPhoto: Any? =
                            screenModel.selectedImageBytes ?: screenModel.currentPhotoUrl

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


                    EditField(
                        "Nama Lengkap",
                        screenModel.nama,
                        screenModel.isEditing,
                        satoshiMedium,
                        Icons.Default.Person
                    ) { screenModel.nama = it }
                    EditField(
                        "Email",
                        screenModel.email,
                        screenModel.isEditing,
                        satoshiMedium,
                        Icons.Default.Email
                    ) { screenModel.email = it }
                    EditField(
                        "Nomor Telepon",
                        screenModel.telepon,
                        screenModel.isEditing,
                        satoshiMedium,
                        Icons.Default.Phone
                    ) { screenModel.telepon = it }

                    Spacer(Modifier.height(40.dp))


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
                        border = if (!screenModel.isEditing) BorderStroke(
                            1.dp,
                            redPrimer
                        ) else null,
                        enabled = !screenModel.isLoading
                    ) {
                        if (screenModel.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            val text =
                                if (screenModel.isEditing) "Simpan Perubahan" else "Edit Profil"
                            val color = if (screenModel.isEditing) Color.White else redPrimer
                            Text(text, color = color, fontFamily = satoshiBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }



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
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "Profil anda telah diperbarui.",
                        fontFamily = satoshiMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {

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
                            modifier = Modifier.fillMaxWidth(0.7f)
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
            Text(
                label,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                fontFamily = font
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = font),
                leadingIcon = {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (enabled) Color(0xFFD53B1E) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
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