package org.fedsal.finance.ui.debtdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.debtdetail.composables.DebtItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DebtDetailScreen(
    viewModel: DebtDetailViewModel = koinViewModel(),
    sourceId: Int,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.init(sourceId)
    }

    val uiState by viewModel.uiState.collectAsState()
    Surface(Modifier.fillMaxSize().safeDrawingPadding().padding(20.dp)) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.debts) {
                DebtItem(debt = it)
            }
        }
    }
}
