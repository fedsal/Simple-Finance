package org.fedsal.finance

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.ui.common.theme.AppTheme
import org.fedsal.finance.ui.expenses.category.ExpensesByCategoryScreen
import org.fedsal.finance.ui.home.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        AppTheme {
            Surface {
                //HomeScreen(rememberNavController())
                ExpensesByCategoryScreen()
            }
        }
    }
}
