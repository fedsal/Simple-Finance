package org.fedsal.finance.ui.home.allcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.common.DateManager

class ExpensesViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    fun initViewModel() {
        viewModelScope.launch {
            DateManager.selectedMonth.collectLatest { month ->
                getExpensesByCategory(month)
            }
        }
        getPaymentMethods()
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
                            val totalBudget = expensesByCategory.sumOf { it.category.budget }
                            val spentBudget = totalBudget - totalSpent
                            val expensesWithCategories = expensesByCategory.map { outerExpenseByCategory ->
                                outerExpenseByCategory.copy(
                                    expensesList = outerExpenseByCategory.expensesList.map { expense -> expense.copy(category = outerExpenseByCategory.category) }
                                )
                            }
                            _uiState.value = uiState.value.copy(
                                isLoading = false,
                                expenses = expensesByCategory,
                                simpleExpenses = expensesWithCategories.flatMap { it.expensesList }
                                    .sortedByDescending { it.date },
                                totalSpent = totalSpent,
                                totalBudget = totalBudget,
                                spentBudget = spentBudget,
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

    private fun getPaymentMethods() {
        viewModelScope.launch {
            runCatching {
                paymentMethodRepository.readWithFlow()
            }.onSuccess { methodsFlow ->
                methodsFlow.collectLatest { methods ->
                    _uiState.value = uiState.value.copy(paymentMethods = methods)
                }
            }.onFailure { e ->
                _uiState.value = uiState.value.copy(error = e.message)
            }
        }
    }
}
