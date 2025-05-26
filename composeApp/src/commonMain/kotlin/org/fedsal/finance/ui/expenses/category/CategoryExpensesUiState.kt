package org.fedsal.finance.ui.expenses.category

import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense

data class CategoryExpensesUiState(
    val isLoading: Boolean = false,
    val category: Category = Category(),
    val expenses: List<Expense> = emptyList(),
    val error: String? = null,
)
