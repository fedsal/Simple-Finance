package org.fedsal.finance.ui.home.composables

import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.fedsal.finance.ui.common.composables.modals.categorydata.CategoryDataModalContent
import org.fedsal.finance.ui.common.composables.modals.expenseinfo.ExpenseInfoModalContent
import org.fedsal.finance.ui.common.composables.modals.selectcategory.SelectCategoryModalContent

// Home bottom sheet
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
                    ExpenseInfoModalContent(
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
                CategoryDataModalContent(onSuccess = {
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
