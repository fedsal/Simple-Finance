package org.fedsal.finance.ui.common.composables.modals.paymentmethod

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.PaymentMethodType
import org.fedsal.finance.domain.models.PaymentMethodType.Companion.getName
import org.fedsal.finance.ui.common.composables.CustomEditText
import org.fedsal.finance.ui.common.composables.SelectableChip
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.opaqueColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreatePaymentMethodContent(
    viewModel: CreatePaymentMethodViewModel = koinViewModel(),
    showCreditOnly: Boolean = false,
    onDismissRequest: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.shouldContinue) {
        onDismissRequest()
    }

    val iconData = listOf(
        AppIcons.PERSON to "Persona",
        AppIcons.CARD to "Tarjeta",
        AppIcons.BANK to "Banco"
    )

    var paymentMethodName by remember { mutableStateOf("") }
    var selectedPaymentMethodType: PaymentMethodType? by remember { mutableStateOf(null) }
    var selectedIcon: AppIcons? by remember { mutableStateOf(null) }
    var titleError by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Box {

        // Done button to close the modal
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = "Done",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .padding(end = 24.dp, bottom = 20.dp)
                .clickable {
                    // Validate inputs
                    titleError = paymentMethodName.isBlank()
                    error = when {
                        selectedPaymentMethodType == null -> "Debe seleccionar un tipo de pago"
                        selectedIcon == null -> "Debe seleccionar un icono"
                        else -> ""
                    }

                    if (!titleError && error.isEmpty()) {
                        viewModel.create(
                            name = paymentMethodName,
                            paymentMethodType = selectedPaymentMethodType!!,
                            iconId = selectedIcon!!
                        )
                    }
                }
        )

        Column(Modifier.padding(20.dp)) {
            // Error toast
            if (error.isNotEmpty()) {
                Box(
                    modifier = Modifier.padding(top = 20.dp, bottom = 12.dp).fillMaxWidth()
                        .height(30.dp)
                        .background(
                            opaqueColor(Color.Red).copy(alpha = .3f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        error,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Red.copy(alpha = .7f)
                        )
                    )
                }
            }
            // Content
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Nuevo método de pago",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))

            // Name
            CustomEditText(
                modifier = Modifier.fillMaxWidth(),
                label = "Nombre",
                placeHolder = "Ingrese el nombre",
                onValueChange = {
                    paymentMethodName = it
                },
                value = paymentMethodName,
                isError = titleError,
            )
            Spacer(Modifier.height(30.dp))
            // Payment method type

            val filteredEntries = if (showCreditOnly) {
                PaymentMethodType.entries.filter { it == PaymentMethodType.CREDIT || it == PaymentMethodType.LOAN}
            } else {
                PaymentMethodType.entries
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Tipo de pago:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredEntries.forEach { paymentMethodType ->
                    SelectableChip(
                        text = paymentMethodType.getName(),
                        isSelected = paymentMethodType == selectedPaymentMethodType,
                        onClick = {
                            selectedPaymentMethodType = paymentMethodType
                        }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            // Payment method icon
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Icono:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                iconData.forEach { (icon, label) ->
                    SelectableChip(
                        text = label,
                        icon = getIcon(icon.name),
                        isSelected = selectedIcon == icon,
                        onClick = {
                            selectedIcon = icon
                        }
                    )
                }
            }
        }
    }
}
