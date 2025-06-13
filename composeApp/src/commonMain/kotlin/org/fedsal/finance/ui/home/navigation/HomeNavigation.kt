package org.fedsal.finance.ui.home.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import org.fedsal.finance.ui.balance.BalanceScreen
import org.fedsal.finance.ui.home.allcategories.ExpensesScreen
import org.fedsal.finance.ui.main.navigation.AppDestinations

@Composable
fun HomeNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onNavigateOuterHome: (AppDestinations) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeDestination.HomeGraph
    ) {
        navigation<HomeDestination.HomeGraph>(startDestination = HomeDestination.Home) {
            composable<HomeDestination.Home>(
                enterTransition = { EnterTransition.None }
            ) {
                ExpensesScreen(onNavigateToCategory = { categoryId ->
                    onNavigateOuterHome(AppDestinations.Category(categoryId))
                })
            }
            composable<HomeDestination.Balance>(
                enterTransition = { EnterTransition.None }
            ) { BalanceScreen() }
        }
    }
}
