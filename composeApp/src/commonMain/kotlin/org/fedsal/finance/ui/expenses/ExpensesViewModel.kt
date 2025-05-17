package org.fedsal.finance.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase

class ExpensesViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    fun initViewModel() {
        getExpenses()
    }

    private fun getExpenses() = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        try {
            getExpensesByCategoryUseCase(
                params = GetExpensesByCategoryUseCase.Params,
                onError = {
                    throw it
                },
                onSuccess = { categories ->
                    val totalSpent = categories.sumOf { it.totalSpent }
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        expenses = categories,
                        totalSpent = totalSpent
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
