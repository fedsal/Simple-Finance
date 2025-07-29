package org.fedsal.finance.ui.home.allexpenses

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.ui.common.DisplayInfoMode
import org.fedsal.finance.ui.common.composables.EditSelectorBottomSheet
import org.fedsal.finance.ui.common.composables.ExpenseItem
import org.fedsal.finance.ui.common.composables.PaymentMethodFilter
import org.fedsal.finance.ui.common.composables.modals.expenseinfo.ExpenseInfoModalContent
import org.fedsal.finance.ui.common.convertFromIso
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpensesView(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    paymentMethods: List<PaymentMethod>,
    onExpenseEdited: (Expense) -> Unit,
    onExpenseDeleted: (Expense) -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf(-1) }

    var showContextualMenu by remember { mutableStateOf(false) }
    var showingExpenseId by remember { mutableStateOf(-1L) }
    var editingExpense by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Contextual menu for editing or deleting expenses
    if (showContextualMenu && showingExpenseId != -1L) {
        EditSelectorBottomSheet(
            sheetState, onDismissRequest = { showContextualMenu = false },
            onEditSelected = {
                showContextualMenu = false
                editingExpense = true
            },
            onDeleteSelected = {
                expenses.find { it.id.toLong() == showingExpenseId }?.let {
                    onExpenseDeleted(it)
                }
                showContextualMenu = false
            }
        )
    }

    if (editingExpense) {
        ModalBottomSheet(
            onDismissRequest = {
                editingExpense = false
                showingExpenseId = -1L
            },
            sheetState = sheetState,
        ) {
            Box(modifier = Modifier.height(600.dp)) {
                ExpenseInfoModalContent(
                    categoryId = expenses.find { it.id.toLong() == showingExpenseId }?.category?.id?.toLong()
                        ?: -1L,
                    mode = DisplayInfoMode.EDIT,
                    expenseId = showingExpenseId,
                    onDismissRequest = {
                        editingExpense = false
                    },
                    onNewPaymentMethodClicked = {
                    }
                )
            }
        }
    }

    Box {
        Column(modifier = modifier) {
            // Payment method filter
            PaymentMethodFilter(
                modifier = Modifier.padding(horizontal = 10.dp),
                items = paymentMethods,
                onItemSelected = {
                    selectedPaymentMethod = it?.id ?: -1
                }
            )
            Spacer(Modifier.height(20.dp))

            val filteredExpenses = if (selectedPaymentMethod != -1) {
                expenses.filter { it.paymentMethod.id == selectedPaymentMethod }
            } else {
                expenses
            }

            if (filteredExpenses.isEmpty()) {
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
                    items(filteredExpenses) {
                        ExpenseItem(
                            modifier = Modifier.padding(horizontal = 10.dp).pointerInput(it.id) {
                                detectTapGestures(
                                    onLongPress = { _ ->
                                        showContextualMenu = true
                                        showingExpenseId = it.id.toLong()
                                    }
                                )
                            },
                            title = it.title,
                            amount = it.amount,
                            paymentMethod = it.paymentMethod,
                            icon = getIcon(it.category.iconId),
                            iconTint = hexToColor(it.category.color),
                            date = convertFromIso(it.date)
                        )
                    }
                }
            }
        }
    }
}
