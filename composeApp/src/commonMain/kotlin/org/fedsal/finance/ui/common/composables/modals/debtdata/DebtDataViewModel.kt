package org.fedsal.finance.ui.common.composables.modals.debtdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType
import org.fedsal.finance.ui.common.DisplayInfoMode

class DebtDataViewModel(
    private val debtRepository: DebtRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : ViewModel() {
    data class UIState(
        val isLoading: Boolean = false,
        val shouldContinue: Boolean = false,
        val paymentMethods: List<PaymentMethod> = emptyList(),
        val debt: Debt? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    private lateinit var mode: DisplayInfoMode
    private var debtId: Long = -1

    fun initViewModel(mode: DisplayInfoMode, debtId: Long = -1) {
        getPaymentMethods()
        this.mode = mode
        this.debtId = debtId
        if (mode == DisplayInfoMode.EDIT && debtId != -1L) {
            getDebtInfo(debtId)
        }
    }

    private fun getDebtInfo(debtId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            debtRepository.getDebtById(debtId).let { debt ->
                _uiState.update { it.copy(isLoading = false, debt = debt) }
            }
        }.onFailure { exception ->
            _uiState.update { it.copy(isLoading = false, error = exception.message) }
            exception.printStackTrace()
        }
    }

    private fun getPaymentMethods() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        runCatching { paymentMethodRepository.read() }
            .onSuccess { paymentMethods ->
                _uiState.update { uiState ->
                    uiState.copy(
                        paymentMethods = paymentMethods.filter {
                            it.type == PaymentMethodType.CREDIT ||
                                    it.type == PaymentMethodType.LOAN
                        },
                        isLoading = false
                    )
                }
            }
            .onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
                exception.printStackTrace()
            }
    }

    fun createOrUpdateDebt(
        title: String,
        amount: Double,
        date: String,
        installments: Int,
        paymentMethod: PaymentMethod,
        description: String,
        category: Category
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            val debt = Debt(
                id = this@DebtDataViewModel.debtId.toInt(),
                title = title,
                amount = amount,
                date = date,
                installments = installments,
                paymentMethod = paymentMethod,
                description = description,
                category = category
            )
            if (mode == DisplayInfoMode.EDIT) {
                debtRepository.update(debt)
            } else {
                debtRepository.createDebt(debt.copy(id = 0))
            }
            _uiState.update { it.copy(isLoading = false, shouldContinue = true) }
        }.onFailure { exception ->
            _uiState.update { it.copy(isLoading = false, error = exception.message) }
            exception.printStackTrace()
        }
    }

    fun dispose() {
        // Clean up resources if needed
        _uiState.value = UIState() // Reset the state
    }

}
