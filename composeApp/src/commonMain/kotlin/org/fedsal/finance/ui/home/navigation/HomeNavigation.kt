package org.fedsal.finance.ui.home.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import org.fedsal.finance.ui.home.allcategories.ExpensesScreen

@Composable
fun HomeNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
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
                    navController.navigate(
                        route = CategoryExpenses(categoryId),
                        navOptions = navOptions { launchSingleTop = true }
                    )
                })
            }
            composable<HomeDestination.Balance>(
                enterTransition = { EnterTransition.None }
            ) { Text("Balance") }
        }
    }
}
