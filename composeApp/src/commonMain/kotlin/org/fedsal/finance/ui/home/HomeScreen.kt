package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.ui.common.composables.BottomNavigation
import org.fedsal.finance.ui.common.navigation.SimpleFinanceNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(navController) {  }
        }
    ) {
        SimpleFinanceNavigation(navController = navController)
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
