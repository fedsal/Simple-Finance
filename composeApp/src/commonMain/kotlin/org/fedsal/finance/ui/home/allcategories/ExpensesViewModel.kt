package org.fedsal.finance.ui.home.allcategories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.DefaultCategories
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.common.DateManager

class ExpensesViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase,
    private val expenseRepository: ExpenseRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    fun initViewModel() {
        observeData()
        getPaymentMethods()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            combine(
                DateManager.selectedMonth,
                DateManager.selectedYear
            ) { month, year -> month to year }
                .debounce(250) // Evita ruido al scrollear rápido meses
                .flatMapLatest { (month, year) ->
                    _uiState.update { it.copy(isLoading = true, selectedMonth = month) }

                    // Llamamos al useCase que devuelve un Flow
                    getExpensesByCategoryUseCase.execute(
                        GetExpensesByCategoryUseCase.Params(month, year)
                    )
                }
                .collect { expensesByCategory ->
                    // Procesamos los datos una sola vez aquí
                    val totalSpent = expensesByCategory.sumOf { it.totalSpent }
                    val totalBudget = expensesByCategory.sumOf { it.category.budget }

                    val expensesWithCategories = expensesByCategory.map { item ->
                        item.copy(
                            expensesList = item.expensesList.map { it.copy(category = item.category) }
                        )
                    }

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            expenses = expensesByCategory,
                            simpleExpenses = expensesWithCategories.flatMap { it.expensesList }
                                .sortedByDescending { it.date },
                            totalSpent = totalSpent,
                            totalBudget = totalBudget,
                            spentBudget = totalBudget - totalSpent
                        )
                    }
                }
        }
    }

    // Edit expense
    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            runCatching {
                expenseRepository.updateExpense(expense)
            }.onSuccess {
                //getExpensesByCategory(uiState.value.selectedMonth)
            }.onFailure { e ->
                e.printStackTrace()
                _uiState.value = uiState.value.copy(error = e.message)
            }
        }
    }

    // Delete expense
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            runCatching {
                expenseRepository.deleteExpense(expense)
            }.onSuccess {
                //getExpensesByCategory(uiState.value.selectedMonth)
            }.onFailure { e ->
                e.printStackTrace()
                _uiState.value = uiState.value.copy(error = e.message)
            }
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
