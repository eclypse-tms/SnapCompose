package com.example.snapcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.snapcompose.ui.MainScreen
import com.example.snapcompose.ui.MainViewModel
import com.example.snapcompose.ui.theme.SnapComposeTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(coroutineContext = Dispatchers.Default)

        enableEdgeToEdge()
        setContent {
            SnapComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}