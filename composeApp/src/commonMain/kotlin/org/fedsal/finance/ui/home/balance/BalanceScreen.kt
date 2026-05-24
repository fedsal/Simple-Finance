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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.home.composables.ButtonBottomSheet
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel = koinViewModel(),
    onDebtClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.initViewModel()
    }

    if (showBottomSheet) {
        ButtonBottomSheet(
            sheetState = sheetState,
            isOnBalance = true,
            onDismissRequest = { showBottomSheet = false }
        )
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Rounded.Add,
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    contentDescription = "Agregar deuda"
                )
            }
        }
    ) { padding ->
        if (uiState.debts.isEmpty()) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "No hay ninguna deuda",
                    modifier = Modifier.size(140.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No tenes ninguna deuda!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)) {
                PieChart(
                    data = uiState.debts,
                    toPayNextMonth = uiState.toPayNextMonth,
                    onItemClicked = { paymentMethod ->
                        onDebtClick(paymentMethod.id)
                    },
                )
            }
        }
    }
}


