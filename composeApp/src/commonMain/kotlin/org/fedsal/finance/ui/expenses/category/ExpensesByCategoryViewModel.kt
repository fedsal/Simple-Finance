package org.fedsal.finance.ui.expenses.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.ui.common.DateManager
import kotlin.properties.Delegates

class ExpensesByCategoryViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryExpensesUiState())
    val uiState: StateFlow<CategoryExpensesUiState> get() = _uiState
    private var categoryId by Delegates.notNull<Int>()

    fun initViewModel(categoryId: Int) {
        // Initialization logic if needed
        this.categoryId = categoryId
        loadExpenses()
        loadPaymentMethods()
    }

    private fun loadExpenses() = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        runCatching {
            val category =
                categoryRepository.getById(this@ExpensesByCategoryViewModel.categoryId)
                    ?: throw IllegalStateException("Category not found")

            expenseRepository.getExpensesByCategory(
                this@ExpensesByCategoryViewModel.categoryId,
                DateManager.selectedMonth.value,
                DateManager.year
            ).collectLatest { expenses ->
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    category = category,
                    expenses = expenses,
                    totalSpent = expenses.sumOf { it.amount },
                    availableAmount = category.budget - expenses.sumOf { it.amount }
                )
            }
        }.onFailure { e ->
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message ?: "An error occurred"
            )
        }
    }

    private fun loadPaymentMethods() = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        runCatching {
            val paymentMethods = paymentMethodRepository.read()
            _uiState.value = uiState.value.copy(
                isLoading = false,
                paymentMethods = paymentMethods
            )
        }.onFailure { e ->
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message ?: "Failed to load payment methods"
            )
        }
    }
}
