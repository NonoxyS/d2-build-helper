package com.nonoxy.d2buildhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import com.nonoxy.d2buildhelper.presentation.GuidesScreen
import com.nonoxy.d2buildhelper.presentation.HeroGuidesViewModel
import com.nonoxy.d2buildhelper.ui.theme.D2BuildHelperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            D2BuildHelperTheme {
                window.statusBarColor = MaterialTheme.colorScheme.background.toArgb()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val viewModel = hiltViewModel<HeroGuidesViewModel>()
                    val state by viewModel.state.collectAsState()
                    GuidesScreen(state = state)
                }
            }
        }
    }
}