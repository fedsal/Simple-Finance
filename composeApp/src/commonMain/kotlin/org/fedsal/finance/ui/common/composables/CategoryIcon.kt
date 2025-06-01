package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.opaqueColor

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color
) {
    Box(
        modifier = modifier
            .background(
                color = opaqueColor(iconTint),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Category icon",
            tint = iconTint,
            modifier = Modifier.fillMaxSize(.7f)
        )
    }
}
