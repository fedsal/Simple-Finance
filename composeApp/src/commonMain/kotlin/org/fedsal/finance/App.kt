package org.fedsal.finance

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import org.fedsal.finance.framework.koin.platformModule
import org.fedsal.finance.framework.koin.provideDataSourceModule
import org.fedsal.finance.framework.koin.provideDatabaseModule
import org.fedsal.finance.framework.koin.provideRepositoryModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = { modules(
        platformModule(),
        provideDataSourceModule,
        provideRepositoryModule,
        provideDatabaseModule
    ) }) {
        MaterialTheme {
        }
    }
}
