package org.fedsal.finance.ui.expenses.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.ExpenseItem
import org.fedsal.finance.ui.common.composables.PaymentMethodFilter
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.expenses.category.composables.CategoryHeader
import org.koin.compose.koinInject

@Composable
fun ExpensesByCategoryScreen(
    categoryId: Int = 0,
    viewModel: ExpensesByCategoryViewModel = koinInject(),
    onNavigateBack: () -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.initViewModel(categoryId)
    }

    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .clickable { onNavigateBack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Volver",
                        modifier = Modifier.height(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "VOLVER",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            CategoryHeader(
                modifier = Modifier.padding(horizontal = 10.dp),
                category = uiState.value.category,
                iconTint = hexToColor(uiState.value.category.color),
                icon = getIcon(uiState.value.category.iconId),
                totalSpent = uiState.value.totalSpent,
                availableAmount = uiState.value.availableAmount,
                onEditPressed = { /*TODO*/ },
            )
            Spacer(Modifier.height(20.dp))
            PaymentMethodFilter(
                items = emptyList(),
                onItemSelected = {}
            )
            Spacer(Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(uiState.value.expenses) {
                    ExpenseItem(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = it.title,
                        amount = it.amount,
                        paymentMethod = it.paymentMethod,
                        icon = getIcon(uiState.value.category.iconId),
                        iconTint = hexToColor(uiState.value.category.color)
                    )
                }
            }
        }
    }
}
