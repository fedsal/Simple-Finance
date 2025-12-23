package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FilterRow(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedItem: Int = -1,
    onItemSelected: (Int?) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = spacedBy(8.dp),
        content = {
            item {
                Surface(
                    modifier = Modifier.clickable {
                        onItemSelected(-1)
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
                SelectableChip(
                    text = items[index],
                    icon =  null,
                    isSelected = selectedItem == index,
                    onClick = {
                        onItemSelected(index)
                    }
                )
            }
        }
    )
}
