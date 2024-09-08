package com.example.snapcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.snapcompose.ui.album.AlbumScreen
import com.example.snapcompose.ui.album.AlbumViewModel
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AlbumViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = AlbumViewModel(coroutineContext = Dispatchers.Default)

        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AlbumScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = viewModel
                )
            }
        }
    }
}