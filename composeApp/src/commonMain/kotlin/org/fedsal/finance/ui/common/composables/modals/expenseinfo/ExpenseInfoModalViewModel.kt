package org.fedsal.finance.ui.common.composables.modals.expenseinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.ui.common.DisplayInfoMode
import org.fedsal.finance.ui.common.convertFromIso
import org.fedsal.finance.ui.common.convertToIso

class ExpenseInfoModalViewModel(
    private val expenseRepository: ExpenseRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    data class UIState(
        val isLoading: Boolean = false,
        val shouldContinue: Boolean = false,
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val category: Category = Category(),
        val expense: Expense = Expense(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState
    private lateinit var mode: DisplayInfoMode
    private var expenseId: Long = -1

    fun initViewModel(categoryId: Long, mode: DisplayInfoMode, expenseId: Long) {
        getPaymentMethods()
        getCategory(categoryId)
        this.mode = mode
        this.expenseId = expenseId

        if (mode == DisplayInfoMode.EDIT && expenseId != -1L) {
            getExpenseInfo(expenseId)
        }
    }

    private fun getExpenseInfo(expenseId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                expenseRepository.getExpenseById(expenseId).let { expense ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            expense = expense.copy(date = convertFromIso(expense.date))
                        )
                    }
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
                exception.printStackTrace()
            }
        }
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

    private fun getCategory(categoryId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            categoryRepository.getById(categoryId.toInt())?.let { category ->
                _uiState.update { it.copy(isLoading = false, category = category) }
            } ?: throw Exception("Category not found")
        }.onFailure { exception ->
            _uiState.update { it.copy(isLoading = false, error = exception.message) }
            exception.printStackTrace()
        }
    }

    fun execute(
        category: Category,
        title: String,
        amount: Double,
        date: String,
        paymentMethod: PaymentMethod,
        description: String
    ) {
        if (mode == DisplayInfoMode.CREATE) {
            addExpense(category, title, amount, date, paymentMethod, description)
        } else if (mode == DisplayInfoMode.EDIT) {
            editExpense(expenseId, title, amount, date, paymentMethod, description, category)
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
    private fun addExpense(
        category: Category,
        title: String,
        amount: Double,
        date: String,
        paymentMethod: PaymentMethod,
        description: String
    ) = viewModelScope.launch {
        val year = Clock.System.todayIn(TimeZone.currentSystemDefault()).year
        // Logic to add expense using the repository
        val expense = Expense(
            title = title,
            category = category,
            amount = amount,
            date = convertToIso("$date$year"),
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
     * Edits an existing expense in the repository.
     */
    private fun editExpense(
        expenseId: Long,
        title: String,
        amount: Double,
        date: String,
        paymentMethod: PaymentMethod,
        description: String,
        category: Category
    ) = viewModelScope.launch {
        val year = Clock.System.todayIn(TimeZone.currentSystemDefault()).year
        // Logic to edit expense using the repository
        val expense = Expense(
            id = expenseId.toInt(),
            title = title,
            amount = amount,
            date = convertToIso("$date$year"),
            paymentMethod = paymentMethod,
            description = description,
            category = category
        )
        runCatching { expenseRepository.updateExpense(expense) }
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

    fun deletePaymentMethod(paymentMethod: PaymentMethod) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            paymentMethodRepository.delete(paymentMethod)
            _uiState.update { it.copy(paymentMethods = it.paymentMethods - paymentMethod) }
        }.onFailure { exception ->
            _uiState.update { it.copy(error = exception.message) }
            exception.printStackTrace()
        }
    }
}
