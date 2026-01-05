package org.fedsal.finance.ui.common.composables.modals.expenseinfo

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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
import org.fedsal.finance.ui.common.opaqueColor
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun ExpenseInfoModalContent(
    addExpenseModalViewModel: ExpenseInfoModalViewModel = koinViewModel(),
    categoryId: Long,
    onDismissRequest: () -> Unit,
    mode: DisplayInfoMode = DisplayInfoMode.CREATE,
    expenseId: Long = -1,
    onNewPaymentMethodClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        addExpenseModalViewModel.initViewModel(categoryId, mode, expenseId)
    }
    val uiState by addExpenseModalViewModel.uiState.collectAsState()
    if (uiState.shouldContinue) {
        onDismissRequest()
        addExpenseModalViewModel.dispose()
    }

    val currentDate by remember { mutableStateOf(DateManager.getCurrentDateOrSelectedMonth()) }

    val paymentMethods = uiState.paymentMethods

    Box(Modifier.fillMaxSize()) {
        var title by remember { mutableStateOf("") }
        var importAmount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(currentDate) }
        var selectedMethod by remember { mutableStateOf(-1) }
        var description by remember { mutableStateOf("") }

        LaunchedEffect(uiState.expense) {
            if (mode == DisplayInfoMode.EDIT) {
                title = uiState.expense.title
                importAmount = uiState.expense.amount.roundToInt().toString()
                date = uiState.expense.date
                description = uiState.expense.description
                selectedMethod =
                    paymentMethods.indexOfFirst { it.id == uiState.expense.paymentMethod.id }
            }
        }

        // Error toast
        if (uiState.error.isNullOrEmpty().not()) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(40.dp)
                    .background(
                        MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = uiState.error ?: "Error desconocido",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                )
                Spacer(Modifier.weight(1f))
                IconButton({ addExpenseModalViewModel.consumeError() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 20.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(50.dp)
                        .clickable {
                            if (title.isBlank()) {
                                addExpenseModalViewModel.postError("Ingrese el titulo")
                                return@clickable
                            }
                            if (importAmount.isBlank() || importAmount.toDoubleOrNull() == null) {
                                addExpenseModalViewModel.postError("Ingrese un importe valido")
                                return@clickable
                            }
                            if (date.isBlank()) {
                                addExpenseModalViewModel.postError("Ingrese una fecha valida")
                                return@clickable
                            }
                            if (selectedMethod < 0 || selectedMethod >= paymentMethods.size) {
                                addExpenseModalViewModel.postError("Seleccione un metodo de pago")
                                return@clickable
                            }
                            addExpenseModalViewModel.execute(
                                category = uiState.category,
                                title = title,
                                amount = importAmount.toDoubleOrNull() ?: 0.0,
                                date = date,
                                paymentMethod = paymentMethods[selectedMethod],
                                description = description
                            )
                        },
                )
            }
            CategoryIcon(
                modifier = Modifier.size(50.dp),
                icon = getIcon(uiState.category.iconId),
                iconTint = hexToColor(uiState.category.color)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = uiState.category.title,
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
                    DashedChip(
                        modifier = Modifier.height(50.dp).width(120.dp)
                    ) { onNewPaymentMethodClicked() }
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
