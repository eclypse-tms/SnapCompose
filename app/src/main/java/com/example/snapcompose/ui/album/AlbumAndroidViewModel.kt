package com.example.snapcompose.ui.album

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcompose.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * This variant inherits from [AndroidViewModel] and has access to the application context
 */
class AlbumAndroidViewModel(private val appContext: Application,
                            private val coroutineContext: CoroutineContext
): AndroidViewModel(appContext) {
    //region View State
    private val _albumViewState: MutableStateFlow<AlbumViewState> = MutableStateFlow(
        AlbumViewState()
    )
    val viewStateFlow: StateFlow<AlbumViewState>
        get() = _albumViewState
    //endregion

    fun onEvent(intent: Intent) = viewModelScope.launch(coroutineContext) {
        when(intent) {
            is Intent.OnPermissionGranted -> {
                // Create an empty image file in the app's cache directory
                val file = File.createTempFile(
                    "temp_image_file_", /* prefix */
                    ".jpg", /* suffix */
                    appContext.cacheDir  /* cache directory */
                )

                // Create sandboxed url for this temp file - needed for the camera API
                val uri = FileProvider.getUriForFile(appContext,
                    "${BuildConfig.APPLICATION_ID}.provider",
                    file
                )
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = uri)
            }

            is Intent.OnPermissionDenied -> {
                // maybe log the permission denial event
                println("User did not grant permission to use the camera")
            }

            is Intent.OnFinishPickingImages -> {
                if (intent.imageUrls.isNotEmpty()) {
                    // Handle picked images
                    val newImages = mutableListOf<ImageBitmap>()
                    for (eachImageUrl in intent.imageUrls) {
                        val inputStream = appContext.contentResolver.openInputStream(eachImageUrl)
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

                    val currentViewState = _albumViewState.value
                    val newCopy = currentViewState.copy(
                        selectedPictures = (currentViewState.selectedPictures + newImages),
                        tempFileUrl = null
                    )
                    _albumViewState.value = newCopy
                } else {
                    // user did not pick anything
                }
            }

            is Intent.OnImageSaved -> {
                val tempImageUrl = _albumViewState.value.tempFileUrl
                if (tempImageUrl != null) {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(appContext.contentResolver, tempImageUrl)

                    val currentPictures = _albumViewState.value.selectedPictures.toMutableList()
                    currentPictures.add(ImageDecoder.decodeBitmap(source).asImageBitmap())

                    _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null,
                        selectedPictures = currentPictures)
                }
            }

            is Intent.OnImageSavingCanceled -> {
                _albumViewState.value = _albumViewState.value.copy(tempFileUrl = null)
            }

            is Intent.OnFinishPickingImagesWith -> {
                // unnecessary in this viewmodel variant
            }

            is Intent.OnPermissionGrantedWith -> {
                // unnecessary in this viewmodel variant
            }

            is Intent.OnImageSavedWith -> {
                // unnecessary in this viewmodel variant
            }
        }
    }
}