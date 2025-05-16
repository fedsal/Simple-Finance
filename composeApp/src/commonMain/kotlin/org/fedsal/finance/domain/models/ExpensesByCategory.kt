package org.fedsal.finance.domain.models

data class ExpensesByCategory(
    val category: Category,
    val expensesList: List<Expense>,
    val totalSpent: Double,
    val availableAmount: Double,
)
