package org.fedsal.finance.ui.debtdetail.modal

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.ui.common.DateDefaults.DATE_MASK
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.composables.visualtransformations.MaskVisualTransformation
import org.fedsal.finance.ui.common.composables.visualtransformations.rememberCurrencyVisualTransformation
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailModal(
    viewModel: DebtDetailModalViewModel = koinInject(),
    debt: Debt,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(debt) {
        viewModel.init(debt.id)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                uiState.debt.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Cuotas pagas:",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(30.dp))
            // Installments
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                // Minus button
                Box(
                    Modifier.size(44.dp).border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    ).clickable { viewModel.onMinusInstallment() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Remover cuota",
                    )
                }
                // Installments label
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 40.dp),
                    text = "${uiState.debt.paidInstallments} de ${uiState.debt.installments}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                // Plus button
                Box(
                    Modifier.size(44.dp).border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    ).clickable { viewModel.onPlusInstallment() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar cuota",
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
            // Amount and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currencyVisualTransformation =
                    rememberCurrencyVisualTransformation(currency = "USD")
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = uiState.debt.amount.toString(),
                    onValueChange = {},
                    label = "Importe",
                    placeHolder = "$ --- ---",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number,
                    visualTransformation = currencyVisualTransformation,
                    enabled = false
                )
                CustomEditText(
                    modifier = Modifier.width(180.dp),
                    value = uiState.debt.date,
                    onValueChange = {},
                    visualTransformation = MaskVisualTransformation(DATE_MASK),
                    label = "Fecha",
                    placeHolder = "DD/MM",
                    textAlign = TextAlign.Center,
                    keyboardType = KeyboardType.Number,
                    enabled = false
                )
            }
            Spacer(Modifier.height(16.dp))
            // Description
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.debt.description,
                onValueChange = {},
                label = "Descripcion",
                placeHolder = "Descripción",
                keyboardType = KeyboardType.Number,
                enabled = false
            )

            // BOTTOM PADDING
            Spacer(Modifier.height(60.dp))
        }
    }
}
