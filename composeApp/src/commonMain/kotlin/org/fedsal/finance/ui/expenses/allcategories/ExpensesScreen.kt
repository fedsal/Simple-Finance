package org.fedsal.finance.ui.expenses.allcategories

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.fedsal.finance.ui.common.composables.SpentHeader
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.expenses.allcategories.composables.ExpenseCategoryItem
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun ExpensesScreen(
    viewModel: ExpensesViewModel = koinViewModel(),
    onNavigateToCategory: (categoryId: Int) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.initViewModel()
    }

    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
        topBar = {
            DateFilterHeader(
                onPreviousClicked = { viewModel.onEvent(ExpensesUIEvent.OnMonthDecremented) },
                onNextClicked = { viewModel.onEvent(ExpensesUIEvent.OnMonthIncremented) },
                dateString = uiState.value.selectedMonth.name.uppercase()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            Spacer(Modifier.height(10.dp))
            // TODO: Handle decimal values
            SpentHeader(totalSpent = "$ ${uiState.value.totalSpent.formatDecimal()}")
            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(uiState.value.expenses) {
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
