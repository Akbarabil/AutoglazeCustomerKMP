package com.example.autoglazecustomer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties

@Composable
fun LoadingDialog(color: Color, message: String = "Mohon tunggu...") {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = color)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = message, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    getLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    satoshiMedium: FontFamily,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    autoExpand: Boolean = false,
    isError: Boolean = false,
    onTextChanged: ((String) -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }
    val redPrimer = Color(0xFFD53B1E)

    var textFieldWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(selectedItem) {
        if (selectedItem != null) {
            val labelStr = getLabel(selectedItem)
            if (searchText != labelStr) {
                searchText = labelStr
            }
        } else {
            searchText = ""
        }
    }

    LaunchedEffect(items) {
        if (autoExpand && items.isNotEmpty() && enabled) {
            expanded = true
        }
    }

    val filteredItems = remember(searchText, items) {
        if (searchText.isBlank()) {
            items
        } else {
            items.filter {
                getLabel(it).contains(searchText, ignoreCase = true)
            }
        }
    }

    Column {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                if (!enabled) return@OutlinedTextField
                searchText = it
                expanded = true
                onTextChanged?.invoke(it)
            },
            enabled = enabled,
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused && enabled) {
                        expanded = true
                    }
                }
                .onGloballyPositioned {
                    textFieldWidth = it.size.width
                },
            label = { Text(label, fontFamily = satoshiMedium) },
            leadingIcon = leadingIcon,
            trailingIcon = {
                if (searchText.isNotEmpty() && enabled) {
                    IconButton(onClick = {
                        searchText = ""
                        expanded = true
                        onTextChanged?.invoke("")
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.DarkGray,
                focusedLabelColor = Color.DarkGray,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.DarkGray,
                errorBorderColor = redPrimer,
                errorLabelColor = redPrimer,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledBorderColor = Color.LightGray,
                disabledLabelColor = Color.LightGray,
                selectionColors = TextSelectionColors(
                    handleColor = Color.DarkGray,
                    backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
                ),
            )
        )

        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(density) { textFieldWidth.toDp() })
                .heightIn(max = 280.dp)
                .background(Color.White),
            properties = PopupProperties(focusable = false)
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 250.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = redPrimer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sedang memuat...", fontFamily = satoshiMedium, color = Color.Gray)
                    }
                } else if (filteredItems.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Data tidak ditemukan",
                                fontFamily = satoshiMedium,
                                color = Color.Gray
                            )
                        },
                        onClick = {}
                    )
                } else {
                    filteredItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(getLabel(item), fontFamily = satoshiMedium) },
                            onClick = {
                                onItemSelected(item)
                                searchText = getLabel(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}