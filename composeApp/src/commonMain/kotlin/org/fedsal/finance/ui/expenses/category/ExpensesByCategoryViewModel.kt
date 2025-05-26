package org.fedsal.finance.ui.expenses.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.util.date.Month
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import kotlin.properties.Delegates

class ExpensesByCategoryViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryExpensesUiState())
    val uiState: StateFlow<CategoryExpensesUiState> get() = _uiState
    private var categoryId by Delegates.notNull<Int>()

    fun initViewModel(categoryId: Int) {
        // Initialization logic if needed
        this.categoryId = categoryId
        loadExpenses()
    }

    private fun loadExpenses() = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        try {
            val category =
                categoryRepository.getById(this@ExpensesByCategoryViewModel.categoryId)
                    ?: throw IllegalStateException("Category not found")
            val expenses =
                expenseRepository.getExpensesByCategory(
                    this@ExpensesByCategoryViewModel.categoryId,
                    Month.MAY,
                    2025
                )
            _uiState.value = uiState.value.copy(
                isLoading = false,
                category = category,
                expenses = expenses
            )
        } catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message ?: "An error occurred"
            )
        }
    }
}
