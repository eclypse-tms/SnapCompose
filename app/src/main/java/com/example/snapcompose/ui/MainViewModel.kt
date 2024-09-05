package com.example.snapcompose.ui

import android.content.Context
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcompose.BuildConfig
import com.example.snapcompose.ui.theme.MainScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

class MainViewModel(private val applicationContext: Context): ViewModel() {
    //region View State
    private val _mainScreenViewState: MutableStateFlow<MainScreenViewState> = MutableStateFlow(
        MainScreenViewState()
    )
    val viewStateFlow: StateFlow<MainScreenViewState>
        get() = _mainScreenViewState
    //endregion


    fun onEvent(event: Event) = viewModelScope.launch {
        when(event) {
            is Event.OnPermissionGranted -> {
                // Create an image file name
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val imageFileName = "JPEG_" + timeStamp + "_"
                val file = File.createTempFile(
                    imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    applicationContext.cacheDir  /* cache directory */
                )

                val uri = FileProvider.getUriForFile(
                    Objects.requireNonNull(applicationContext),
                    BuildConfig.APPLICATION_ID + ".provider", file
                )
                _mainScreenViewState.value = _mainScreenViewState.value.copy(tempFileUrl = uri)
            }

            is Event.OnPickPhotoRequest -> {
                // Handle pick photo request
            }

            is Event.OnImageSavedToTempFile -> {
                val tempImageUrl = _mainScreenViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source = ImageDecoder.createSource(applicationContext.contentResolver, tempImageUrl)

                    val currentPictures = _mainScreenViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(ImageDecoder.decodeBitmap(source).asImageBitmap())

                    _mainScreenViewState.value = _mainScreenViewState.value.copy(tempFileUrl = null, selectedPictures = currentPictures)
                }
            }
        }
    }
}