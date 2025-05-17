package org.fedsal.finance.ui.expenses.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpenseCategoryItem(
    modifier: Modifier = Modifier,
    categoryName: String,
    totalSpent: String,
    availableAmount: String,
    onClick: () -> Unit = {},
    icon: ImageVector,
    iconTint: Color,
) {
    Box(contentAlignment = Alignment.TopCenter, modifier = modifier.clickable { onClick.invoke() }) {
        Surface(
            modifier = Modifier.padding(top = 20.dp).width(180.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = totalSpent,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = availableAmount,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.Green.copy(alpha = .8f)
                )
            }
        }
        Box(
            modifier = Modifier.size(35.dp)
                .background(
                    color = opaqueColor(iconTint),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$categoryName icon",
                tint = iconTint,
                modifier = modifier.size(25.dp)
            )
        }
    }
}

fun opaqueColor(color: Color, factor: Float = 0.55f): Color {
    return lerp(color, Color.Black, factor.coerceIn(0f, 1f))
}
