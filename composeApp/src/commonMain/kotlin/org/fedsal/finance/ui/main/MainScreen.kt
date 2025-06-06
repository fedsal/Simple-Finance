package org.fedsal.finance.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.ui.main.navigation.AppNavigation

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}
