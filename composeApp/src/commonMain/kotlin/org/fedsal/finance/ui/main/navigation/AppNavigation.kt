package org.fedsal.finance.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.toRoute
import org.fedsal.finance.ui.category.ExpensesByCategoryScreen
import org.fedsal.finance.ui.common.navigation.animatedComposable
import org.fedsal.finance.ui.home.navigation.CategoryExpenses

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppDestinations.HOME
    ) {


        // Category details screen
        animatedComposable<CategoryExpenses> { backStackEntry ->
            val categoryExpenses: CategoryExpenses = backStackEntry.toRoute()
            ExpensesByCategoryScreen(categoryExpenses.id) {
                navController.navigateUp()
            }
        }
    }
}
