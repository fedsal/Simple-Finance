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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.fedsal.finance.ui.common.DateManager
import org.fedsal.finance.ui.common.composables.BottomNavigation
import org.fedsal.finance.ui.common.composables.DateFilterHeader
import org.fedsal.finance.ui.common.navigation.HomeDestination
import org.fedsal.finance.ui.common.navigation.SimpleFinanceNavigation
import org.fedsal.finance.ui.common.navigation.hasRoute
import org.fedsal.finance.ui.home.composables.AddExpenseModalContent
import org.fedsal.finance.ui.home.composables.CreateCategoryModalContent
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
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val showHeader = navBackStackEntry.hasRoute(HomeDestination.HomeGraph)
            val month = DateManager.selectedMonth.collectAsState()
            if (showHeader) {
                DateFilterHeader(
                    onPreviousClicked = { DateManager.decrementMonth() },
                    onNextClicked = { DateManager.incrementMonth() },
                    dateString = month.value.name.uppercase()
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        bottomBar = {
            BottomNavigation(navController) {
                showBottomSheet = true
            }
        }
    ) { padding ->
        if (showBottomSheet) {
            ButtonBottomSheet(sheetState, onDismissRequest = { showBottomSheet = false })
        }
        SimpleFinanceNavigation(
            modifier = Modifier.padding(top = padding.calculateTopPadding(), bottom = 72.dp),
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
    var creatingCategory by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Box(modifier = Modifier.height(600.dp)) {
            var categoryId: Long? by remember { mutableStateOf(null) }

            androidx.compose.animation.AnimatedVisibility(
                visible = categoryId != null,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }
                ),
            ) {
                categoryId?.let { safeCategory ->
                    AddExpenseModalContent(
                        categoryId = safeCategory,
                        onDismissRequest = onDismissRequest
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = creatingCategory,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth }
                ),
            ) {
                CreateCategoryModalContent(onCategoryCreated = {
                    categoryId = it
                    creatingCategory = false
                })
            }

            if (categoryId == null && !creatingCategory) {
                // Show the category selection modal content
                SelectCategoryModalContent(
                    onCategoryClicked = { categoryId = it.id.toLong() },
                    onNewCategoryClicked = { creatingCategory = true },
                )
            }
        }
    }
}
