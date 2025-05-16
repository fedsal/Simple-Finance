package org.fedsal.finance.ui.expenses

import org.fedsal.finance.domain.models.ExpensesByCategory

data class ExpensesUIState(
    val isLoading: Boolean = false,
    val expenses: List<ExpensesByCategory> = emptyList(),
    val totalSpent: Double = 0.0,
    val error: String? = null,
)
