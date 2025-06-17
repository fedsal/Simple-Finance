package org.fedsal.finance.ui.debtdetail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.ui.common.composables.CategoryIcon
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor

@Composable
fun DebtItem(
    modifier: Modifier = Modifier,
    debt: Debt
) {
    Row(
        modifier = modifier
            .height(100.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(16.dp)
            ),
    ) {
        CategoryIcon(
            modifier = Modifier.size(50.dp),
            icon = getIcon(debt.category.iconId),
            iconTint = hexToColor(debt.category.color)
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
            // Category title chip
            Box(
                Modifier.background(
                    color = hexToColor(debt.category.color),
                    RoundedCornerShape(8.dp)
                ).padding(8.dp)
            ) { Text(text = debt.category.title, style = MaterialTheme.typography.titleSmall) }
            // Debt title
            Text(
                text = debt.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
            // Installments
            val currentInstallment = debt.paidInstallments + 1
            val installmentImport = debt.amount / debt.installments
            Text(
                text = "Cuota $currentInstallment de ${debt.installments}. ($ ${installmentImport.formatDecimal()})",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }

        // Total debt
        Text(
            text = "$ ${debt.amount.formatDecimal()}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}
