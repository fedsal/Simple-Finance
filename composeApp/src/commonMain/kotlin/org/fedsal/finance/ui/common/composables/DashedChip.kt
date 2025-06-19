package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DashedChip(modifier: Modifier = Modifier,onClick: () -> Unit) {
    val tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .4f)
    Box(
        modifier = modifier
            .drawBehind {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f)
                drawRoundRect(
                    color = tint,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = pathEffect),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Category",
            tint = tint,
            modifier = Modifier.size(30.dp)
        )
    }
}

