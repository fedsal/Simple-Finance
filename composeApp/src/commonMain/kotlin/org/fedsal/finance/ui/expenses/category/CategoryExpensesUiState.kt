package org.fedsal.finance.ui.expenses.category

import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod

data class CategoryExpensesUiState(
    val isLoading: Boolean = false,
    val category: Category = Category(),
    val expenses: List<Expense> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val totalSpent: Double = 0.0,
    val availableAmount: Double = 0.0,
    val error: String? = null,
)
