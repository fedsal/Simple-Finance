package org.fedsal.finance.ui.common.composables.modals.debtdata

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
import org.fedsal.finance.domain.models.DefaultPaymentMethods
import org.fedsal.finance.ui.common.DateDefaults.DATE_LENGTH
import org.fedsal.finance.ui.common.DateDefaults.DATE_MASK
import org.fedsal.finance.ui.common.DateManager
import org.fedsal.finance.ui.common.ExpenseDefaults
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.composables.modals.expenseinfo.PaymentMethodChip
import org.fedsal.finance.ui.common.composables.visualtransformations.MaskVisualTransformation
import org.fedsal.finance.ui.common.composables.visualtransformations.rememberCurrencyVisualTransformation

@Composable
fun DebtDataModalContent(categoryId: Long, onDismissRequest: () -> Unit) {
    Box {
        val currentDate by remember { mutableStateOf(DateManager.getCurrentDate()) }
        val paymentMethods = listOf(DefaultPaymentMethods.CREDIT_CARD)

        // Component state variables
        var title by remember { mutableStateOf("") }
        var importAmount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(currentDate) }
        var selectedMethod by remember { mutableStateOf(-1) }
        var installments by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        // Errors
        var titleError by remember { mutableStateOf(false) }
        var importError by remember { mutableStateOf(false) }
        var dateError by remember { mutableStateOf(false) }
        var installmentsError by remember { mutableStateOf(false) }

        // Done button to close the modal
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = "Done",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .padding(end = 24.dp, bottom = 20.dp)
                .clickable {
                    onDismissRequest()
                }
        )

        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Agregar una deuda",
                textAlign = TextAlign.Center,
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
                isError = titleError
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
                        val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
                        if (trimmed.isEmpty() || trimmed.toInt() <= ExpenseDefaults.MAX_EXPENSE_VALUE) {
                            importAmount = trimmed
                        }
                    },
                    label = "Importe",
                    placeHolder = "$ --- ---",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number,
                    visualTransformation = currencyVisualTransformation,
                    isError = importError
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
                    keyboardType = KeyboardType.Number,
                    isError = dateError
                )
            }
            Spacer(Modifier.height(16.dp))
            // Installments
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                value = installments,
                onValueChange = { installments = it },
                label = "Cuotas",
                placeHolder = "Cantidad de cuotas",
                keyboardType = KeyboardType.Number,
                isError = installmentsError
            )
            Spacer(Modifier.height(16.dp))
            // Payment method
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Metodo de pago:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            // Payment methods list
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(paymentMethods.size) { index ->
                    PaymentMethodChip(
                        paymentMethod = paymentMethods[index],
                        isSelected = index == selectedMethod,
                        onClick = { selectedMethod = index }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            // Description
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = "Descripcion",
                placeHolder = "Ingrese la descripción",
                keyboardType = KeyboardType.Number,
                isError = installmentsError
            )
        }
    }
}
