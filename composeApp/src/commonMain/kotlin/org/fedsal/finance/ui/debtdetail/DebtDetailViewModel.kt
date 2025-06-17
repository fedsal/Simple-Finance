package org.fedsal.finance.ui.debtdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod

class DebtDetailViewModel(
    private val debtRepository: DebtRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {

    data class UIState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val debts: List<Debt> = emptyList(),
        val totalDebt: Double = 0.0,
        val source: PaymentMethod = PaymentMethod()
    )

    private var _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun init(sourceId: Int) {
        getPaymentMethod(sourceId)
        loadDebts(sourceId)
    }

    private fun getPaymentMethod(sourceId: Int) {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            viewModelScope.launch {
                paymentMethodRepository.getById(sourceId).collectLatest { paymentMethod ->
                    _uiState.update {
                        it.copy(
                            source = paymentMethod
                                ?: throw IllegalArgumentException("Payment method not found")
                        )
                    }
                }
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load payment method"
                )
            }
        }
    }

    private fun loadDebts(sourceId: Int) {
        _uiState.update { it.copy(isLoading = true) }

        runCatching {
            viewModelScope.launch {
                debtRepository.getDebtsByPaymentMethod(sourceId).collectLatest { debts ->
                    _uiState.update {
                        UIState(
                            isLoading = false,
                            debts = debts,
                            totalDebt = debts.sumOf { items -> items.amount },
                            source = it.source
                        )
                    }
                }
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }
}
