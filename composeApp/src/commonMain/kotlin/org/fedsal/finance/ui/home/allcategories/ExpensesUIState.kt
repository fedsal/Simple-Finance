package org.fedsal.finance.ui.home.allcategories

import kotlinx.datetime.Month
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.ExpensesByCategory
import org.fedsal.finance.domain.models.PaymentMethod

data class ExpensesUIState(
    val isLoading: Boolean = false,
    val expenses: List<ExpensesByCategory> = emptyList(),
    val simpleExpenses: List<Expense> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val selectedMonth: Month = Month.JANUARY,
    val totalSpent: Double = 0.0,
    val totalBudget: Double = 0.0,
    val spentBudget: Double = 0.0,
    val error: String? = null,
)
