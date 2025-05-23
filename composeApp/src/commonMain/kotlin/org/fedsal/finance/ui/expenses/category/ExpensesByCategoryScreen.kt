package org.fedsal.finance.ui.expenses.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType
import org.fedsal.finance.ui.common.composables.ExpenseItem
import org.fedsal.finance.ui.common.composables.PaymentMethodFilter
import org.fedsal.finance.ui.expenses.category.composables.CategoryHeader

@Composable
fun ExpensesByCategoryScreen() {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        contentWindowInsets = WindowInsets.safeGestures,
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding(), start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Volver",
                    modifier = Modifier.height(20.dp).clickable { }
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "VOLVER",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { }
                )
            }
            Spacer(Modifier.height(16.dp))
            CategoryHeader(
                modifier = Modifier.padding(horizontal = 10.dp),
                category = Category(
                    id = 1,
                    title = "Food",
                    iconId = "food_icon",
                    color = "0xFF000000",
                    budget = 250000.0,
                ),
                iconTint = Color.Green,
                icon = Icons.Default.AccountBalanceWallet,
                totalSpent = 200000.0,
                availableAmount = 50000.0,
                onEditPressed = { /*TODO*/ },
            )
            Spacer(Modifier.height(20.dp))
            val paymentMethods = listOf(
                PaymentMethod(
                    id = 1,
                    name = "Efectivo",
                    iconId = AppIcons.CASH.name,
                    color = "F000000",
                    type = PaymentMethodType.CASH
                ),
                PaymentMethod(
                    id = 1,
                    name = "Credit",
                    iconId = AppIcons.CARD.name,
                    color = "FF000000",
                    type = PaymentMethodType.CREDIT
                )
            )
            PaymentMethodFilter(
                items = paymentMethods,
                onItemSelected = {}
            )
            Spacer(Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(10) {
                    ExpenseItem(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = "Compra super",
                        amount = 32000.0,
                        paymentMethod = paymentMethods[0],
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = Color.Green
                    )
                }
            }
        }
    }
}
