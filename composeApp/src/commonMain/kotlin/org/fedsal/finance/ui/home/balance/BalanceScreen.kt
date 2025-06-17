package org.fedsal.finance.ui.home.balance

import PieChart
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel = koinViewModel(),
    onDebtClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initViewModel()
    }

    Surface(modifier = Modifier.safeDrawingPadding()) {

        if (uiState.debts.isEmpty()) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "No debts",
                    modifier = Modifier.size(140.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No tenes ninguna deuda!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Surface
        }
        Column(Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)) {
            PieChart(
                data = uiState.debts,
                onItemClicked = { paymentMethod ->
                    onDebtClick(paymentMethod.id)
                },
            )
        }
    }
}


