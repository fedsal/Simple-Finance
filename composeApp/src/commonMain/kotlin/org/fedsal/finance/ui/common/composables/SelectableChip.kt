package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SelectableChip(
    text: String,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val color = if (isSelected) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
    var longPressed by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier.clickable(onClick = onClick).height(50.dp).width(120.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClick.invoke() }, onLongPress = {
                        longPressed = true
                        GlobalScope.launch(Dispatchers.IO) {
                            delay(5000)
                            longPressed = false
                        }
                    })
                },
            border = BorderStroke(
                width = 2.dp,
                color = color
            ),
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = color
                    )
                }
                Spacer(Modifier.width(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    ),
                )
            }
        }

        // The delete button positioned outside the surface
        if (onDelete != null && longPressed) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .offset(x = 8.dp, y = (-8).dp) // half-outside top-right
                    .size(20.dp).align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    )
                )
            }
        }
    }
}
