package com.example.snapcompose.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcompose.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val coroutineContext: CoroutineContext
): ViewModel() {

    //region View State
    private val _mainScreenViewState: MutableStateFlow<MainScreenViewState> = MutableStateFlow(
        MainScreenViewState()
    )
    val viewStateFlow: StateFlow<MainScreenViewState>
        get() = _mainScreenViewState
    //endregion

    fun onEvent(event: Event) = viewModelScope.launch(coroutineContext) {
        when(event) {
            is Event.OnPermissionGrantedWith -> {
                // Create an empty image file in the app's cache directory
                val file = File.createTempFile(
                    "temp_image_file_", /* prefix */
                    ".jpg", /* suffix */
                    event.compositionContext.cacheDir  /* cache directory */
                )

                // Create sandboxed url for this temp file - needed for the camera API
                val uri = FileProvider.getUriForFile(event.compositionContext,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    file
                )
                _mainScreenViewState.value = _mainScreenViewState.value.copy(tempFileUrl = uri)
            }

            is Event.OnPermissionDenied -> {
                // maybe log the permission denial event
                println("User did not grant permission to use the camera")
            }

            is Event.OnFinishPickingImagesWith -> {
                if (event.imageUrls.isNotEmpty()) {
                    // Handle picked images
                    val newImages = mutableListOf<ImageBitmap>()
                    for (eachImageUrl in event.imageUrls) {
                        val inputStream = event.compositionContext.contentResolver.openInputStream(eachImageUrl)
                        val bytes = inputStream?.readBytes()
                        inputStream?.close()

                        if (bytes != null) {
                            val bitmapOptions = BitmapFactory.Options()
                            bitmapOptions.inMutable = true
                            val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            val imageBitmap = bitmap.asImageBitmap()
                            newImages.add(imageBitmap)
                        } else {
                            // error reading the bytes from the image url
                            println("The image that was picked could not be read from the device at this url: $eachImageUrl")
                        }
                    }

                    val currentViewState = _mainScreenViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures = (currentViewState.selectedPictures + newImages),
                        tempFileUrl = null
                    )
                    _mainScreenViewState.value = newCopy
                } else {
                    // user did not pick anything
                }
            }

            is Event.OnImageSavedWith -> {
                val tempImageUrl = _mainScreenViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source = ImageDecoder.createSource(event.compositionContext.contentResolver, tempImageUrl)

                    val currentPictures = _mainScreenViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(ImageDecoder.decodeBitmap(source).asImageBitmap())

                    _mainScreenViewState.value = _mainScreenViewState.value.copy(tempFileUrl = null,
                        selectedPictures = currentPictures)
                }
            }

            is Event.OnImageSavingCanceled -> {
                _mainScreenViewState.value = _mainScreenViewState.value.copy(tempFileUrl = null)
            }

            is Event.OnPermissionGranted -> {
                // unnecessary in this viewmodel variant
            }

            is Event.OnFinishPickingImages -> {
                // unnecessary in this viewmodel variant
            }

            is Event.OnImageSaved -> {
                // unnecessary in this viewmodel variant
            }
        }
    }
}