package org.fedsal.finance.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType

class ExpensesViewModel(
    private val repository: ExpenseRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUIState())
    val uiState: StateFlow<ExpensesUIState> get() = _uiState

    init {
        runBlocking {
            repository.createExpense(
                Expense(
                    id = 0,
                    category = Category(
                        title = "Compras",
                        iconId = "compras",
                        color = "#FF0000",
                        budget = 500000.0,
                        id = 1
                    ),
                    amount = 1500000.0,
                    date = "12/10/22",
                    description = "Compras de supermercado",
                    title = "Compras de supermercado",
                    paymentMethod = PaymentMethod(
                        id = 1,
                        name = "Tarjeta de credito",
                        iconId = "tarjeta_credito",
                        color = "#FF0000",
                        type = PaymentMethodType.CREDIT
                    )
                )
            )
        }
    }

    fun initViewModel() {
        getExpenses()
    }

    private fun getExpenses() = viewModelScope.launch {
        _uiState.value = uiState.value.copy(isLoading = true)
        try {
            val categories = repository.getAllExpensesByCategory()
            val totalSpent = categories.sumOf { it.totalSpent }
            _uiState.value = uiState.value.copy(
                isLoading = false,
                expenses = categories,
                totalSpent = totalSpent
            )
        } catch (e: Exception) {
            _uiState.value = uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
}
