package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DateFilterHeader(
    dateString: String,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp).height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(50.dp).clickable { onPreviousClicked() },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Previous",
        )
        Text(
            text = dateString,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge
        )
        Icon(
            modifier = Modifier.size(50.dp).clickable { onNextClicked() },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Next",
        )
    }

}

@Composable
@Preview
fun DateFilterHeaderPreview() {
    AppTheme {
        DateFilterHeader(
            dateString = "Abril 2025",
            onPreviousClicked = {},
            onNextClicked = {}
        )
    }
}
