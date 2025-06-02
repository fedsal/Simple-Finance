package org.fedsal.finance.ui.home.composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.ui.common.convertToIso

class AddExpenseModalViewModel(
    private val expenseRepository: ExpenseRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {

    data class UIState(
        val isLoading: Boolean = false,
        val shouldContinue: Boolean = false,
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun initViewModel() {
        getPaymentMethods()
    }

    private fun getPaymentMethods() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        runCatching { paymentMethodRepository.read() }
            .onSuccess { paymentMethods ->
                _uiState.update { it.copy(paymentMethods = paymentMethods, isLoading = false) }
            }
            .onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
                exception.printStackTrace()
            }
    }

    /**
     * Adds an expense to the repository.
     *
     * @param category The category of the expense.
     * @param title The title of the expense.
     * @param amount The amount of the expense.
     * @param date The date of the expense.
     * @param paymentMethod The payment method used for the expense.
     * @param description A description of the expense.
     */
    fun addExpense(
        category: Category,
        title: String,
        amount: Double,
        date: String,
        paymentMethod: PaymentMethod,
        description: String
    ) = viewModelScope.launch {
        // Logic to add expense using the repository
        val expense = Expense(
            title = title,
            category = category,
            amount = amount,
            date = convertToIso(date),
            paymentMethod = paymentMethod,
            description = description
        )
        runCatching { expenseRepository.createExpense(expense) }
            .onSuccess { _uiState.update { it.copy(isLoading = false, shouldContinue = true) } }
            .onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
                exception.printStackTrace()
            }
    }

    /**
     * Disposes of the ViewModel resources.
     */
    fun dispose() {
        _uiState.value = UIState()
    }
}
