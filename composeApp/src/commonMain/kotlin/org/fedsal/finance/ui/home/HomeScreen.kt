package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.ui.common.DateManager
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.fedsal.finance.ui.home.composables.BottomNavigation
import org.fedsal.finance.ui.home.composables.ButtonBottomSheet
import org.fedsal.finance.ui.home.navigation.HomeNavigation
import org.fedsal.finance.ui.main.navigation.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateOuterHome: (AppDestinations) -> Unit
) {
    val navController = rememberNavController()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        topBar = {
            val month = DateManager.selectedMonth.collectAsState()
            DateFilterHeader(
                onPreviousClicked = { DateManager.decrementMonth() },
                onNextClicked = { DateManager.incrementMonth() },
                dateString = month.value.name.uppercase()
            )

        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(navController) {
                showBottomSheet = true
            }
        }
    ) { padding ->
        if (showBottomSheet) {
            ButtonBottomSheet(sheetState, onDismissRequest = { showBottomSheet = false })
        }
        HomeNavigation(
            modifier = Modifier.padding(padding),
            navController = navController,
            onNavigateOuterHome = onNavigateOuterHome
        )
    }
}
