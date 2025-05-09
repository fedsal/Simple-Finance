package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.fedsal.finance.ui.common.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DateFilterHeader(
    dateString: String,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(30.dp).clickable { onPreviousClicked() },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
            contentDescription = "Previous",
        )
        Text(
            text = dateString,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize =  20.sp, fontWeight = FontWeight.SemiBold)
        )
        Icon(
            modifier = Modifier.size(30.dp).clickable { onNextClicked() },
            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
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
