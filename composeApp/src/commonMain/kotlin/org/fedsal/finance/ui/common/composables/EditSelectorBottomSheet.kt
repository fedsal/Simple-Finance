package org.fedsal.finance.ui.common.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSelectorBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onEditSelected: () -> Unit,
    onDeleteSelected: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp)
                    .clickable(onClick = onEditSelected),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Editar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp)
                    .clickable(onClick = onEditSelected),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Eliminar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red.copy(alpha = .6f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
