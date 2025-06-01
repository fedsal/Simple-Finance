package org.fedsal.finance.ui.home

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.ui.common.composables.BottomNavigation
import org.fedsal.finance.ui.common.navigation.SimpleFinanceNavigation
import org.fedsal.finance.ui.home.composables.AddExpenseModalContent
import org.fedsal.finance.ui.home.composables.SelectCategoryModalContent
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController
) {
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
            ButtonBottomSheet(sheetState, onDismissRequest = { showBottomSheet = false })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Box(modifier = Modifier.height(600.dp)) {
            var category: Category? by remember { mutableStateOf(null) }

            androidx.compose.animation.AnimatedVisibility(
                visible = category != null,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }
                ),
            ) {
                category?.let { safeCategory ->
                    AddExpenseModalContent(
                        category = safeCategory,
                        onDismissRequest = onDismissRequest
                    )
                }
            }
            if (category == null) {
                // Show the category selection modal content
                SelectCategoryModalContent(
                    onCategoryClicked = { category = it },
                    onNewCategoryClicked = { /* TODO */ }
                )
            }
        }
    }
}
