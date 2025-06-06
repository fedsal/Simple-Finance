package org.fedsal.finance

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.fedsal.finance.ui.common.theme.AppTheme
import org.fedsal.finance.ui.main.MainScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        AppTheme {
            Surface { MainScreen() }
        }
    }
}
