package org.fedsal.finance.ui.expenses.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.expenses.category.composables.CategoryHeader

@Composable
fun ExpensesByCategoryScreen() {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            CategoryHeader(
                category = Category(
                    id = 1,
                    title = "Food",
                    iconId = "food_icon",
                    color = "0xFF000000",
                    budget = 250000.0,
                ),
                iconTint = Color.Green,
                icon = Icons.Default.AccountBalanceWallet,
                totalSpent = 200000.0,
                availableAmount = 50000.0,
                onEditPressed = { /*TODO*/ },
            )
        }
    }
}
