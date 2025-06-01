package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomEditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeHolder: String,
) {
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.align(Alignment.TopStart).padding(top = 8.dp),
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeHolder) },
            singleLine = true,
            maxLines = 1,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 22.dp)
                .background(color = MaterialTheme.colorScheme.surfaceContainerLow).padding(horizontal = 8.dp)
        )
    }
}
