package org.fedsal.finance.ui.home

import androidx.compose.runtime.Composable
import org.fedsal.finance.ui.expenses.ExpensesScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen() {
    ExpensesScreen()
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
