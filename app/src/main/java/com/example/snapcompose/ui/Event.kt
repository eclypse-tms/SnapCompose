package com.example.snapcompose.ui

import android.content.Context
import android.net.Uri

sealed class Event {
    data object OnPermissionGranted: Event()

    data class OnPermissionGrantedWith(val compositionContext: Context): Event()

    data object OnPermissionDenied: Event()

    data object OnImageSaved: Event()

    data class OnImageSavedWith (val compositionContext: Context): Event()

    data object OnImageSavingCanceled: Event()

    data class OnFinishPickingImages(val imageUrls: List<Uri>): Event()

    data class OnFinishPickingImagesWith(val compositionContext: Context, val imageUrls: List<Uri>): Event()
}