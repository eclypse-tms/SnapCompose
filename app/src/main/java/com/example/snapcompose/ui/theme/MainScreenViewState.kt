package com.example.snapcompose.ui.theme

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class MainScreenViewState(
    val tempFileUrl: Uri? = null,
    val selectedPictures: List<ImageBitmap> = emptyList(),
)