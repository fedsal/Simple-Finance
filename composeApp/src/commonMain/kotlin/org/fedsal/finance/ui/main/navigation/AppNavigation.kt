package org.fedsal.finance.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import org.fedsal.finance.ui.category.ExpensesByCategoryScreen
import org.fedsal.finance.ui.common.navigation.animatedComposable
import org.fedsal.finance.ui.home.HomeScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppDestinations.Home
    ) {
        // Home screen destination
        composable<AppDestinations.Home> {
            HomeScreen { destination ->
                navController.navigate(destination) { launchSingleTop = true }
            }
        }
        // Category details destination
        animatedComposable<AppDestinations.Category> { backStackEntry ->
            val categoryExpenses: AppDestinations.Category = backStackEntry.toRoute()
            ExpensesByCategoryScreen(categoryExpenses.id) {
                navController.navigateUp()
            }
        }
    }

}
