package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.common.composables.AddCategoryButton
import org.fedsal.finance.ui.common.composables.BaseModal
import org.fedsal.finance.ui.common.composables.BottomNavigation
import org.fedsal.finance.ui.common.composables.CategoryItem
import org.fedsal.finance.ui.common.getIcon
import org.fedsal.finance.ui.common.hexToColor
import org.fedsal.finance.ui.common.navigation.SimpleFinanceNavigation
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(navController) {
                showBottomSheet = true
            }
        }
    ) {
        if (showBottomSheet) {
            BaseModal(
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                title = "Agregar un gasto",
            ) {

            }
        }
        SimpleFinanceNavigation(
            modifier = Modifier.padding(bottom = 72.dp),
            navController = navController
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

@Composable
fun SelectCategoryModalContent(
    categories: List<Pair<Double, Category>>,
    onCategoryClicked: (category: Category) -> Unit,
    onNewCategoryClicked: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) {
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
