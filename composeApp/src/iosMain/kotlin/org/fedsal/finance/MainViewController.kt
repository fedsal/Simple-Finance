package org.fedsal.finance

import androidx.compose.ui.window.ComposeUIViewController
import org.fedsal.finance.framework.koin.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) {
    App()
}
