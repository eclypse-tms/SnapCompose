package com.example.snapcompose.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.snapcompose.ui.theme.MainScreenViewState
import com.example.snapcompose.ui.theme.SnapComposeTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun MainScreen(modifier: Modifier = Modifier,
               viewModel: MainViewModel) {

    val currentContext = LocalContext.current

    val viewState: MainScreenViewState by viewModel.viewStateFlow.collectAsState()

    val pickImageFromAlbumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { urls ->
        viewModel.onEvent(Event.OnFinishPickingImagesWith(currentContext, urls))
        // or if you are using AndroidViewModel use this event instead
        // viewModel.onEvent(Event.OnFinishPickingImages(urls))
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isImageSaved ->
        if (isImageSaved) {
            viewModel.onEvent(Event.OnImageSavedWith(currentContext))
            // or if you are using AndroidViewModel use this event instead
            // viewModel.onEvent(Event.OnImageSaved)
        } else {
            // handle image saving error or cancellation
            viewModel.onEvent(Event.OnImageSavingCanceled)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            viewModel.onEvent(Event.OnPermissionGrantedWith(currentContext))
            // or if you are using AndroidViewModel use this event instead
            // viewModel.onEvent(Event.OnPermissionGranted)
        } else {
            // handle permission denied such as:
            viewModel.onEvent(Event.OnPermissionDenied)
            // or perhaps show a toast
            // Toast.makeText(context, "In order to take pictures, you have to allow this app to use your camera", Toast.LENGTH_SHORT).show()
        }
    }

    // this ensures that the camera is launched only once when the url of the temp file changes
    LaunchedEffect(key1 = viewState.tempFileUrl) {
        viewState.tempFileUrl?.let {
            cameraLauncher.launch(it)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)
        .verticalScroll(rememberScrollState())
        .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }) {
            Text(text = "Take a photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            pickImageFromAlbumLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text(text = "Pick a picture")
        }
        if (viewState.selectedPictures.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pictures")
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 1200.dp)
            ) {
                itemsIndexed(viewState.selectedPictures) { index, picture ->
                    Image(
                        modifier = Modifier.padding(8.dp),
                        bitmap = picture,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun MainScreenPreview() {
    val viewModel = MainViewModel(Dispatchers.Default)
    SnapComposeTheme {
        MainScreen(viewModel = viewModel)
    }
}