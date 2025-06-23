package org.fedsal.finance.ui.debtdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.ui.common.DisplayInfoMode
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.composables.EditSelectorBottomSheet
import org.fedsal.finance.ui.common.composables.modals.debtdata.DebtDataModalContent
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.debtdetail.composables.DebtItem
import org.fedsal.finance.ui.debtdetail.modal.DebtDetailModal
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailScreen(
    viewModel: DebtDetailViewModel = koinViewModel(),
    sourceId: Int,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDebt: Debt? by remember { mutableStateOf(null) }
    var showContextualMenu by remember { mutableStateOf(false) }
    var showDebtEdit by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(sourceId)
    }

    if (uiState.debts.isEmpty()) {
        onNavigateBack()
    }

    Surface(Modifier.fillMaxSize().safeDrawingPadding().padding(top = 20.dp, bottom = 10.dp)) {

        // MODALS SECTION

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
        if (selectedDebt != null && !showContextualMenu && !showDebtEdit) {
            DebtDetailModal(
                debt = selectedDebt!!,
                sheetState = sheetState,
                onDismissRequest = { selectedDebt = null },
            )
        }

        // Contextual menu for editing or deleting expenses
        if (showContextualMenu) {
            EditSelectorBottomSheet(
                sheetState, onDismissRequest = { showContextualMenu = false },
                onEditSelected = {
                    showContextualMenu = false
                    showDebtEdit = true
                },
                onDeleteSelected = {
                    selectedDebt?.let { viewModel.deleteDebt(it) }
                    showContextualMenu = false
                    selectedDebt = null
                }
            )
        }
        // Debt edit modal
        if (showDebtEdit) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDebtEdit = false
                    selectedDebt = null
                },
                sheetState = sheetState,
            ) {
                Box(modifier = Modifier.height(600.dp)) {
                    DebtDataModalContent(
                        mode = DisplayInfoMode.EDIT,
                        debtId = selectedDebt?.id?.toLong() ?: -1L,
                        onDismissRequest = { showDebtEdit = false },
                        onNewPaymentMethodClicked = { }
                    )
                }
            }
        }

        Column {

            // Back navigation button
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

            // Header with source information
            Box(
                modifier = Modifier.padding(horizontal = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier.padding(top = 30.dp).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.source.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Saldo pendiente",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$ ${uiState.totalDebt.formatDecimal()}",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                CategoryIcon(
                    modifier = Modifier.size(50.dp),
                    icon = getIcon(uiState.source.iconId),
                    iconTint = hexToColor(uiState.source.color)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Debts list
            LazyColumn(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.debts) {
                    DebtItem(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { _ ->
                                    selectedDebt = it
                                    showContextualMenu = true
                                },
                                onTap = { _ ->
                                    selectedDebt = it
                                }
                            )
                        },
                        debt = it
                    )
                }
            }
        }
    }
}
