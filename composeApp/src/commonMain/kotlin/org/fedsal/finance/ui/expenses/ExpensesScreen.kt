package org.fedsal.finance.ui.expenses

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.fedsal.finance.ui.common.composables.SpentHeader
import org.fedsal.finance.ui.expenses.composables.ExpenseCategoryItem

@Composable
fun ExpensesScreen() {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
        topBar = {
            DateFilterHeader(
                onPreviousClicked = {},
                onNextClicked = {},
                dateString = "Abril 2025"
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
            SpentHeader(totalSpent = "$ 1.500.000")
            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(4) {
                    ExpenseCategoryItem(
                        categoryName = "Compras",
                        totalSpent = "$ 1.500.000",
                        availableAmount = "$ 1.500.000",
                        icon = Icons.Outlined.ShoppingCart,
                        iconTint = Color.Magenta
                    )
                }
            }
        }
    }
}
