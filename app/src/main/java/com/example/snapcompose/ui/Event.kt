package com.example.snapcompose.ui

sealed class Event {
    data object OnPermissionGranted: Event()

    data object OnImageSavedToTempFile: Event()


    data object OnPickPhotoRequest: Event()
}