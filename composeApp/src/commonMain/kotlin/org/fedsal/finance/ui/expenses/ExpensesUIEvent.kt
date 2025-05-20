package org.fedsal.finance.ui.expenses

import org.fedsal.finance.domain.models.Category

sealed class ExpensesUIEvent {
    data object OnErrorConsumed : ExpensesUIEvent()
    data class OnCategorySelected(val category: Category): ExpensesUIEvent()
    data object OnMonthIncremented : ExpensesUIEvent()
    data object OnMonthDecremented : ExpensesUIEvent()
}
