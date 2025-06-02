package org.fedsal.finance.ui.expenses.category.composables

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.common.formatDecimal
import org.fedsal.finance.ui.common.opaqueColor

@Composable
fun CategoryHeader(
    modifier: Modifier = Modifier,
    category: Category,
    icon: ImageVector,
    iconTint: Color,
    totalSpent: Double,
    availableAmount: Double,
    onEditPressed: () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Surface(
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth().height(254.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Icon(
                        modifier = Modifier.clickable { onEditPressed.invoke() },
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Presupuesto: $ ${category.budget.formatDecimal()}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Gastaste",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$ ${totalSpent.formatDecimal()}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Te queda",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$ ${availableAmount.formatDecimal()}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (availableAmount > 0) Color.Green else Color.Red
                    ),
                )
            }
        }
        Box(
            modifier = Modifier.size(50.dp)
                .background(
                    color = opaqueColor(iconTint),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "${category.title} icon",
                tint = iconTint,
                modifier = modifier.size(33.dp)
            )
        }
    }
}
