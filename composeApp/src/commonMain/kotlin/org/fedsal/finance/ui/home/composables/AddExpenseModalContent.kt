package org.fedsal.finance.ui.home.composables

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
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.composables.PaymentMethodChip
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.koin.compose.koinInject

@Composable
fun AddExpenseModalContent(
    addExpenseModalViewModel: AddExpenseModalViewModel = koinInject(),
    category: Category,
    onDismissRequest: () -> Unit,
) {
    LaunchedEffect(Unit) {
        addExpenseModalViewModel.initViewModel()
    }
    val uiState = addExpenseModalViewModel.uiState.collectAsState()
    if (uiState.value.shouldContinue) {
        onDismissRequest()
        addExpenseModalViewModel.dispose()
    }

    val paymentMethods = uiState.value.paymentMethods

    Box(Modifier.fillMaxSize()) {
        var title by remember { mutableStateOf("") }
        var importAmount by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }
        var selectedMethod by remember { mutableStateOf(-1) }
        var description by remember { mutableStateOf("") }

        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = "Done",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .padding(end = 24.dp)
                .clickable {
                    if (selectedMethod >= 0)
                        addExpenseModalViewModel.addExpense(
                            category = category,
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
                icon = getIcon(category.iconId),
                iconTint = hexToColor(category.color)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = category.title,
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
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = importAmount,
                    onValueChange = { importAmount = it },
                    label = "Importe",
                    placeHolder = "$ --- ---",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number
                )
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = date,
                    onValueChange = { date = it },
                    label = "Fecha",
                    placeHolder = "DD/MM/AAAA",
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
                    PaymentMethodChip(
                        paymentMethod = paymentMethods[index],
                        isSelected = index == selectedMethod,
                        baseColor = MaterialTheme.colorScheme.surfaceBright,
                        selectedColor = MaterialTheme.colorScheme.surfaceContainer,
                        onClick = { selectedMethod = index }
                    )
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
