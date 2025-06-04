package org.fedsal.finance.ui.expenses.allcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.common.DateManager

class ExpensesViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    fun initViewModel() {
        viewModelScope.launch {
            DateManager.selectedMonth.collectLatest { month ->
                getExpensesByCategory(month)
            }
        }
    }

    private fun getExpensesByCategory(month: Month) {
        _uiState.value = uiState.value.copy(isLoading = true)
        runCatching {
            getExpensesByCategoryUseCase(
                params = month,
                onError = {
                    throw it
                },
                onSuccess = { categories ->
                    viewModelScope.launch {
                        categories.collectLatest { expensesByCategory ->
                            val totalSpent = expensesByCategory.sumOf { it.totalSpent }
                            _uiState.value = uiState.value.copy(
                                isLoading = false,
                                expenses = expensesByCategory,
                                totalSpent = totalSpent,
                                selectedMonth = month
                            )
                        }
                    }
                }
            )
        }.onFailure { e ->
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
}
