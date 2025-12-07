package org.fedsal.finance.ui.categoryExpenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.PaymentMethod
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

    private fun loadExpenses(filter: PaymentMethod? = null) = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        runCatching {
            val category =
                categoryRepository.getById(this@ExpensesByCategoryViewModel.categoryId)
                    ?: throw IllegalStateException("Category not found")

            expenseRepository.getExpensesByCategory(
                this@ExpensesByCategoryViewModel.categoryId,
                DateManager.selectedMonth.value,
                DateManager.selectedYear.value
            ).collectLatest { expenses ->
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    category = category,
                    expenses = expenses.filter {
                        if (filter != null) {
                            it.paymentMethod.id == filter.id
                        } else true
                    }.sortedByDescending { it.date },
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

    fun deleteExpense(expenseId: Long) = viewModelScope.launch {
        runCatching {
            val expense = uiState.value.expenses.first { it.id.toLong() == expenseId }
            expenseRepository.deleteExpense(expense)
        }.onFailure { e ->
            _uiState.value = uiState.value.copy(
                error = e.message ?: "Failed to delete expense"
            )
        }
    }

    fun deleteCategory() = viewModelScope.launch {
        runCatching {
            val category = uiState.value.category
            categoryRepository.delete(category)
        }.onFailure { e ->
            _uiState.value = uiState.value.copy(
                error = e.message ?: "Failed to delete category"
            )
        }
    }

    fun filterExpensesByPaymentMethod(paymentMethod: PaymentMethod?) {
        loadExpenses(paymentMethod)
    }
}
