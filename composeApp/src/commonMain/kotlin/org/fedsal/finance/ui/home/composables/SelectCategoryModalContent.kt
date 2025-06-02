package org.fedsal.finance.ui.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.common.composables.AddCategoryButton
import org.fedsal.finance.ui.common.composables.CategoryItem
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.koin.compose.koinInject

@Composable
fun SelectCategoryModalContent(
    selectCategoryViewModel: SelectCategoryViewModel = koinInject(),
    onCategoryClicked: (category: Category) -> Unit,
    onNewCategoryClicked: () -> Unit,
) {

    LaunchedEffect(Unit) {
        selectCategoryViewModel.initViewModel()
    }

    val uiState = selectCategoryViewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Agregar un gasto",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.value.categories) {
                val (spent, category) = it
                CategoryItem(
                    title = category.title,
                    spent = spent,
                    icon = getIcon(category.iconId),
                    iconTint = hexToColor(category.color),
                    onClick = {
                        onCategoryClicked(category)
                    }
                )
            }
            item {
                AddCategoryButton {
                    onNewCategoryClicked()
                }
            }
        }
    }
}
