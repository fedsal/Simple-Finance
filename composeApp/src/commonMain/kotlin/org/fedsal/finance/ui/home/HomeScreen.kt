package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
        topBar = {
            TopAppBar(
                title = { Text("Icon") }
            )
            DateFilterHeader(
                onPreviousClicked = {},
                onNextClicked = {},
                dateString =  "Abril 2025"
            )
        }
    ) {

    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
