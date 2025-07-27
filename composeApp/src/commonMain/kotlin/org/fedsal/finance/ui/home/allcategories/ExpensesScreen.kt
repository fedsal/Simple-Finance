package org.fedsal.finance.ui.home.allcategories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.SpentHeader
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.home.allcategories.composables.ExpenseCategoryItem
import org.fedsal.finance.ui.home.allexpenses.AllExpensesScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = koinViewModel(),
    onNavigateToCategory: (categoryId: Int) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.initViewModel()
    }

    val uiState by viewModel.uiState.collectAsState()
    var showAllExpenses by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            Spacer(Modifier.height(10.dp))
            SpentHeader(
                modifier = Modifier.padding(horizontal = 20.dp),
                totalSpent = "$ ${uiState.totalSpent.formatDecimal()}",
                remainBudget = uiState.spentBudget,
                totalBudget = uiState.totalBudget,
                onClick = { showAllExpenses = !showAllExpenses }
            )
            Spacer(Modifier.height(12.dp))
            if (showAllExpenses) {
                AllExpensesScreen(
                    Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    uiState.simpleExpenses,
                    uiState.paymentMethods
                )
            } else {
                LazyVerticalGrid(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(uiState.expenses) {
                        ExpenseCategoryItem(
                            categoryName = it.category.title,
                            totalSpent = it.totalSpent,
                            availableAmount = it.availableAmount,
                            icon = getIcon(it.category.iconId),
                            iconTint = hexToColor(it.category.color),
                            onClick = {
                                onNavigateToCategory.invoke(it.category.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
