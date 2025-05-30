package org.fedsal.finance.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                SelectCategoryModalContent(
                    categories = listOf(
                        Pair(100.0, Category(title = "food", budget = 2000.0, iconId ="food", color = "#FF0000")),
                        Pair(50.0, Category(title = "food", budget = 2000.0, iconId ="food", color = "#FF0000")),
                        Pair(200.0, Category(title = "food", budget = 2000.0, iconId ="food", color = "#FF0000"))
                    ),
                    onCategoryClicked = { category ->
                        // Handle category selection
                        showBottomSheet = false
                        // Navigate to add expense screen with selected category
                        //navController.navigate("add_expense/${category.id}")
                    },
                    onNewCategoryClicked = {
                        // Handle new category creation
                        showBottomSheet = false
                        //navController.navigate("new_category")
                    }
                )
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
fun AddExpenseModalContent() {

}

@Composable
fun SelectCategoryModalContent(
    categories: List<Pair<Double, Category>>,
    onCategoryClicked: (category: Category) -> Unit,
    onNewCategoryClicked: () -> Unit,
) {
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
            items(categories) {
                val (spent, category) = it
                CategoryItem(
                    title = category.title,
                    spent = spent,
                    icon = getIcon(category.iconId),
                    iconTint = Color.Magenta,
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
