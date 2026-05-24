package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.datetime.LocalDate
import org.fedsal.finance.ui.common.DateManager
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.fedsal.finance.ui.common.getCurrentLocale
import org.fedsal.finance.ui.common.getLocalizedMonthName
import org.fedsal.finance.ui.home.composables.BottomNavigation
import org.fedsal.finance.ui.home.navigation.HomeDestination
import org.fedsal.finance.ui.home.navigation.HomeNavigation
import org.fedsal.finance.ui.home.navigation.hasRoute
import org.fedsal.finance.ui.main.navigation.AppDestinations

@Composable
fun HomeScreen(
    onNavigateOuterHome: (AppDestinations) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isOnBalance = navBackStackEntry.hasRoute(HomeDestination.Balance)
    val isOnExport = navBackStackEntry.hasRoute(HomeDestination.Export)

    Scaffold(
        topBar = {
            if (isOnBalance || isOnExport) return@Scaffold
            val month by DateManager.selectedMonth.collectAsState()
            val localDate = LocalDate(DateManager.selectedYear.value, month, 1)
            DateFilterHeader(
                onPreviousClicked = { DateManager.decrementMonth() },
                onNextClicked = { DateManager.incrementMonth() },
                dateString = getLocalizedMonthName(
                    localDate,
                    getCurrentLocale()
                ).uppercase() + if (DateManager.isInCurrentYear()) "" else " ${DateManager.selectedYear.value}"
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = { BottomNavigation(navController) }
    ) { padding ->
        HomeNavigation(
            modifier = Modifier.padding(padding),
            navController = navController,
            onNavigateOuterHome = onNavigateOuterHome
        )
    }
}
