package org.fedsal.finance.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.util.date.Month
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.expenses.allcategories.ExpensesUIEvent
import org.fedsal.finance.ui.expenses.allcategories.ExpensesUIState

class ExpensesViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    fun initViewModel() {
        getExpensesByCategory(month = Month.MAY)
    }

    fun onEvent(event: ExpensesUIEvent) {
        when (event) {
            is ExpensesUIEvent.OnErrorConsumed -> {
                _uiState.value = uiState.value.copy(error = null)
            }
            is ExpensesUIEvent.OnCategorySelected -> { /* TODO */ }
            ExpensesUIEvent.OnMonthDecremented -> {
                val currentMonth = uiState.value.selectedMonth.ordinal
                val newMonth = if (currentMonth == 0) {
                    Month.entries.first()
                } else {
                    Month.entries[currentMonth - 1]
                }
                getExpensesByCategory(newMonth)
            }
            ExpensesUIEvent.OnMonthIncremented -> {
                val currentMonth = uiState.value.selectedMonth.ordinal
                val newMonth = if (currentMonth == Month.entries.lastIndex) {
                    Month.entries.last()
                } else {
                    Month.entries[currentMonth + 1]
                }
                getExpensesByCategory(newMonth)
            }
        }
    }

    private fun getExpensesByCategory(month: Month) = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        try {
            getExpensesByCategoryUseCase(
                params = GetExpensesByCategoryUseCase.Params(month = month, year = 2023),
                onError = {
                    throw it
                },
                onSuccess = { categories ->
                    val totalSpent = categories.sumOf { it.totalSpent }
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        expenses = categories,
                        totalSpent = totalSpent,
                        selectedMonth = month
                    )
                }
            )
        } catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
}
