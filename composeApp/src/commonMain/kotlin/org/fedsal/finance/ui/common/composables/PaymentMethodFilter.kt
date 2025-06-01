package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.ui.common.getIcon

@Composable
fun PaymentMethodFilter(
    modifier: Modifier = Modifier,
    items: List<PaymentMethod>,
    onItemSelected: (PaymentMethod?) -> Unit
) {
    var selectedItem by remember { mutableStateOf(-1) }
    LazyRow(
        horizontalArrangement = spacedBy(8.dp),
        content = {
            item {
                Surface(
                    modifier = Modifier.clickable {
                        selectedItem = -1
                        onItemSelected(null)
                    }.height(50.dp).width(120.dp),
                    color = if (selectedItem == -1) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "TODOS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
            items(items.size) { index ->
                val item = items[index]
                PaymentMethodChip(
                    paymentMethod = item,
                    isSelected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        onItemSelected(item)
                    }
                )
            }
        }
    )
}

@Composable
fun PaymentMethodChip(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    baseColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    selectedColor: Color = MaterialTheme.colorScheme.surfaceBright,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick).height(50.dp).width(120.dp),
        color = if (isSelected) selectedColor else baseColor,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIcon(paymentMethod.iconId),
                contentDescription = paymentMethod.name
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = paymentMethod.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

