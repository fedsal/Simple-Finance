package org.fedsal.finance.ui.common.composables.modals.expenseinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.DateDefaults.DATE_LENGTH
import org.fedsal.finance.ui.common.DateDefaults.DATE_MASK
import org.fedsal.finance.ui.common.DateManager
import org.fedsal.finance.ui.common.DisplayInfoMode
import org.fedsal.finance.ui.common.ExpenseDefaults
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.composables.DashedChip
import org.fedsal.finance.ui.common.composables.SelectableChip
import org.fedsal.finance.ui.common.composables.visualtransformations.MaskVisualTransformation
import org.fedsal.finance.ui.common.composables.visualtransformations.rememberCurrencyVisualTransformation
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun ExpenseInfoModalContent(
    addExpenseModalViewModel: ExpenseInfoModalViewModel = koinInject(),
    categoryId: Long,
    onDismissRequest: () -> Unit,
    mode: DisplayInfoMode = DisplayInfoMode.CREATE,
    expenseId: Long = -1,
    onNewPaymentMethodClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        addExpenseModalViewModel.initViewModel(categoryId, mode, expenseId)
    }
    val uiState = addExpenseModalViewModel.uiState.collectAsState()
    if (uiState.value.shouldContinue) {
        onDismissRequest()
        addExpenseModalViewModel.dispose()
    }

    val currentDate by remember { mutableStateOf(DateManager.getCurrentDate()) }

    val paymentMethods = uiState.value.paymentMethods

    Box(Modifier.fillMaxSize()) {
        var title by remember { mutableStateOf("") }
        var importAmount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(currentDate) }
        var selectedMethod by remember { mutableStateOf(-1) }
        var description by remember { mutableStateOf("") }

        LaunchedEffect(uiState.value.expense) {
            if (mode == DisplayInfoMode.EDIT) {
                title = uiState.value.expense.title
                importAmount = uiState.value.expense.amount.roundToInt().toString()
                date = uiState.value.expense.date
                description = uiState.value.expense.description
                selectedMethod =
                    paymentMethods.indexOfFirst { it.id == uiState.value.expense.paymentMethod.id }
            }
        }

        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = "Done",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .padding(end = 24.dp)
                .clickable {
                    if (selectedMethod >= 0)
                        addExpenseModalViewModel.execute(
                            category = uiState.value.category,
                            title = title,
                            amount = importAmount.toDoubleOrNull() ?: 0.0,
                            date = date,
                            paymentMethod = paymentMethods[selectedMethod],
                            description = description
                        )
                },
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            CategoryIcon(
                modifier = Modifier.size(50.dp),
                icon = getIcon(uiState.value.category.iconId),
                iconTint = hexToColor(uiState.value.category.color)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = uiState.value.category.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(16.dp))
            // Title
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = "Titulo",
                placeHolder = "Ingrese el titulo",
            )
            Spacer(Modifier.height(16.dp))
            // Import and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currencyVisualTransformation =
                    rememberCurrencyVisualTransformation(currency = "USD")
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = importAmount,
                    onValueChange = { newValue ->
                        /**
                         * Trim entered value removing 0 at start and then remove
                         * every characters that is not a digit
                         * Update value only if it's empty or if value is not higher
                         * than 10000
                         */
                        val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
                        if (trimmed.isEmpty() || trimmed.toInt() <= ExpenseDefaults.MAX_EXPENSE_VALUE) {
                            importAmount = trimmed
                        }
                    },
                    label = "Importe",
                    placeHolder = "$ --- ---",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number,
                    visualTransformation = currencyVisualTransformation
                )
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = date,
                    onValueChange = {
                        if (it.length <= DATE_LENGTH) {
                            date = it
                        }
                    },
                    visualTransformation = MaskVisualTransformation(DATE_MASK),
                    label = "Fecha",
                    placeHolder = "DD/MM",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number
                )
            }
            // Payment method
            Spacer(Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Metodo de pago:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            // Payment methods list
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(paymentMethods.size) { index ->
                    SelectableChip(
                        text = paymentMethods[index].name,
                        icon = getIcon(paymentMethods[index].iconId),
                        isSelected = index == selectedMethod,
                        onClick = { selectedMethod = index },
                        onDelete = {
                            addExpenseModalViewModel.deletePaymentMethod(paymentMethods[index])
                        }
                    )
                }
                item {
                    DashedChip(modifier = Modifier.height(50.dp).width(120.dp)) { onNewPaymentMethodClicked() }
                }
            }
            // Description
            Spacer(Modifier.height(20.dp))
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                placeHolder = "Ingrese la descripción",
            )
        }
    }
}
