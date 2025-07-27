package org.fedsal.finance.ui.home.allexpenses

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import org.fedsal.finance.ui.common.composables.ExpenseItem
import org.fedsal.finance.ui.common.composables.PaymentMethodFilter
import org.fedsal.finance.ui.common.convertFromIso
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor

@Composable
fun AllExpensesScreen(
    modifier: Modifier = Modifier,
    expenses: List<Expense>,
    paymentMethods: List<PaymentMethod>
) {
    var selectedPaymentMethod by remember { mutableStateOf(-1) }

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
                        modifier = Modifier.padding(horizontal = 10.dp).pointerInput(Unit) {
                            detectTapGestures()
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
