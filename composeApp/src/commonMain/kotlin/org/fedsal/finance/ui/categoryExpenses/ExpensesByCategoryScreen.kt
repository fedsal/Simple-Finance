package org.fedsal.finance.ui.categoryExpenses

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import org.fedsal.finance.ui.categoryExpenses.composables.CategoryHeader
import org.fedsal.finance.ui.common.DisplayInfoMode
import org.fedsal.finance.ui.common.composables.EditSelectorBottomSheet
import org.fedsal.finance.ui.common.composables.ExpenseItem
import org.fedsal.finance.ui.common.composables.PaymentMethodFilter
import org.fedsal.finance.ui.common.composables.modals.categorydata.CategoryDataModalContent
import org.fedsal.finance.ui.common.composables.modals.expenseinfo.ExpenseInfoModalContent
import org.fedsal.finance.ui.common.convertFromIso
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
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

    var showContextualMenu by remember { mutableStateOf(false) }
    var showCategoryInfo by remember { mutableStateOf(false) }
    var showExpenseInfo by remember { mutableStateOf(false) }
    var showingExpenseId by remember { mutableStateOf(-1L) }
    var expenseContextualInfo by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Surface(
        modifier = Modifier.safeDrawingPadding().padding(top = 20.dp, bottom = 10.dp),
    ) {
        // Contextual menu for editing or deleting expenses
        if (showContextualMenu) {
            EditSelectorBottomSheet(
                sheetState, onDismissRequest = { showContextualMenu = false },
                onEditSelected = {
                    showContextualMenu = false
                    if (expenseContextualInfo) showExpenseInfo = true
                    else showCategoryInfo = true
                },
                onDeleteSelected = {
                    viewModel.deleteExpense(showingExpenseId)
                    showContextualMenu = false
                    showingExpenseId = -1L
                }
            )
        }

        // Category information modal
        if (showCategoryInfo) {
            ModalBottomSheet(
                onDismissRequest = { showCategoryInfo = false },
                sheetState = sheetState,
            ) {
                Box(modifier = Modifier.height(600.dp)) {
                    CategoryDataModalContent(
                        categoryId = uiState.value.category.id.toLong(),
                        mode = DisplayInfoMode.EDIT,
                        onSuccess = {
                            showCategoryInfo = false
                            viewModel.initViewModel(categoryId)
                        }
                    )
                }
            }
        }

        if (showExpenseInfo) {
            ModalBottomSheet(
                onDismissRequest = {
                    showCategoryInfo = false
                    showExpenseInfo = false
                    expenseContextualInfo = false
                    showingExpenseId = -1L
                },
                sheetState = sheetState,
            ) {
                Box(modifier = Modifier.height(600.dp)) {
                    ExpenseInfoModalContent(
                        categoryId = uiState.value.category.id.toLong(),
                        mode = DisplayInfoMode.EDIT,
                        expenseId = showingExpenseId,
                        onDismissRequest = {
                            showExpenseInfo = false
                            viewModel.initViewModel(categoryId)
                        },
                        onNewPaymentMethodClicked = {
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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

            // Category header
            CategoryHeader(
                modifier = Modifier.padding(horizontal = 10.dp),
                category = uiState.value.category,
                iconTint = hexToColor(uiState.value.category.color),
                icon = getIcon(uiState.value.category.iconId),
                totalSpent = uiState.value.totalSpent,
                availableAmount = uiState.value.availableAmount,
                onOptionsPressed = { showContextualMenu = true },
            )
            Spacer(Modifier.height(20.dp))

            // Payment method filter
            PaymentMethodFilter(
                items = uiState.value.paymentMethods,
                onItemSelected = {
                    viewModel.filterExpensesByPaymentMethod(it)
                }
            )
            Spacer(Modifier.height(20.dp))


            // Expenses section
            if (uiState.value.expenses.isEmpty()) {
                // No expenses message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay ningun gasto!",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }
            } else {
                // Expenses list
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.value.expenses) {
                        ExpenseItem(
                            modifier = Modifier.padding(horizontal = 10.dp).pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = { _ ->
                                        showContextualMenu = true
                                        expenseContextualInfo = true
                                        showingExpenseId = it.id.toLong()
                                    }
                                )
                            },
                            title = it.title,
                            amount = it.amount,
                            paymentMethod = it.paymentMethod,
                            icon = getIcon(uiState.value.category.iconId),
                            iconTint = hexToColor(uiState.value.category.color),
                            showIcon = false,
                            date = convertFromIso(it.date)
                        )
                    }
                }
            }
        }
    }
}
