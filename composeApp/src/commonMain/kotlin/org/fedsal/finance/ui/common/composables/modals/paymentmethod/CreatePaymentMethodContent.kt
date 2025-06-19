package org.fedsal.finance.ui.common.composables.modals.paymentmethod

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.CustomEditText

@Composable
fun CreatePaymentMethodContent(
    onDismissRequest: () -> Unit,
) {
    var paymentMethodName by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Nuevo método de pago",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(30.dp))

        // Name
        CustomEditText(
            modifier = Modifier.fillMaxWidth(),
            label = "Nombre",
            placeHolder = "Ingrese el nombre",
            onValueChange = {},
            value = paymentMethodName
        )
        // Tipo de pago

    }
}
