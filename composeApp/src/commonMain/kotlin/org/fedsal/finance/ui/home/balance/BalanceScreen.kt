package org.fedsal.finance.ui.home.balance

import PieChart
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.DefaultCategories
import org.fedsal.finance.domain.models.DefaultPaymentMethods

@Composable
fun BalanceScreen() {
    Surface(modifier = Modifier.safeDrawingPadding()) {
        val items = listOf(
            Debt(
                id = 1,
                title = "Credit Card",
                amount = 180200.0,
                date = "2023-10-01",
                category = DefaultCategories.FUN,
                installments = 1,
                paymentMethod = DefaultPaymentMethods.CREDIT_CARD,
                description = "Monthly credit card payment"
            ),
            Debt(
                id = 1,
                title = "Cash",
                amount = 1500000.0,
                date = "2023-10-01",
                category = DefaultCategories.FUN,
                installments = 1,
                paymentMethod = DefaultPaymentMethods.CASH,
                description = "Monthly credit card payment"
            ),
        )
        Column(Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)) {
            PieChart(
                data = items
            )
        }
    }
}


