package org.fedsal.finance.ui.expenses

import io.ktor.util.date.Month
import org.fedsal.finance.domain.models.ExpensesByCategory

data class ExpensesUIState(
    val isLoading: Boolean = false,
    val expenses: List<ExpensesByCategory> = emptyList(),
    val selectedMonth: Month = Month.JANUARY,
    val totalSpent: Double = 0.0,
    val error: String? = null,
)
