package org.fedsal.finance.ui.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import org.fedsal.finance.ui.common.composables.CustomEditText

@Composable
fun AddExpenseModalContent() {
    Column {
        CustomEditText(
            value = "",
            onValueChange = {},
            label = "Expense Name",
            placeHolder = "Enter expense name",
        )
    }
}
