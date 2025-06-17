package org.fedsal.finance.ui.home.balance.debtdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.home.balance.debtdetail.composables.DebtItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DebtDetailScreen(
    viewModel: DebtDetailViewModel = koinViewModel(),
    sourceId: Int
) {
    LaunchedEffect(Unit) {
        viewModel.init(sourceId)
    }

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(uiState.debts) {
            DebtItem(debt = it)
        }
    }
}
