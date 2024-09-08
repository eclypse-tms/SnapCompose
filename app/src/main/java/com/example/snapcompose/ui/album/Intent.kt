package com.example.snapcompose.ui.album

import android.content.Context
import android.net.Uri

/**
 * User generated events that can be triggered from the UI.
 */
sealed class Intent {
    data object OnPermissionGranted: Intent()

    data class OnPermissionGrantedWith(val compositionContext: Context): Intent()

    data object OnPermissionDenied: Intent()

    data object OnImageSaved: Intent()

    data class OnImageSavedWith (val compositionContext: Context): Intent()

    data object OnImageSavingCanceled: Intent()

    data class OnFinishPickingImages(val imageUrls: List<Uri>): Intent()

    data class OnFinishPickingImagesWith(val compositionContext: Context, val imageUrls: List<Uri>): Intent()
}