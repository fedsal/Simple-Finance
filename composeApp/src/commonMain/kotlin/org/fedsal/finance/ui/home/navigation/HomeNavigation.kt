package org.fedsal.finance.ui.home.navigation

import PieChart
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Preview with sample data
                    PieChart(
                        data = mapOf(
                            Pair("Sample-1", 150),
                            Pair("Sample-2", 120),
                            Pair("Sample-3", 110),
                            Pair("Sample-4", 170),
                            Pair("Sample-5", 120),
                        )
                    )
                }
            }
        }
    }
}
