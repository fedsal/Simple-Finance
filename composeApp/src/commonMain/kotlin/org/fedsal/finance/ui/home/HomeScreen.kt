package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
        topBar = {
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
