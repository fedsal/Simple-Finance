package org.fedsal.finance.ui.debtdetail.modal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.domain.models.Debt

class DebtDetailModalViewModel(
    private val debtRepository: DebtRepository,
) : ViewModel() {
    data class UIState(
        val isLoading: Boolean = false,
        val debt: Debt = Debt(),
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    private var debtId: Int = 0

    fun init(debtId: Int) {
        this.debtId = debtId
        fetchDebt(debtId)
    }

    private fun fetchDebt(debtId: Int) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        runCatching { debtRepository.getDebtById(debtId.toLong()) }.onSuccess { debt ->
            if (debt == null) throw IllegalArgumentException("Debt not found")
            _uiState.update { it.copy(debt = debt) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Unknown error") }
        }
    }

    fun onPlusInstallment() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (_uiState.value.debt.installments <= _uiState.value.debt.paidInstallments) {
            _uiState.update { it.copy(error = "No more installments to pay") }
            return@launch
        }
        val updatedDebt = _uiState.value.debt.copy(
            paidInstallments = _uiState.value.debt.paidInstallments + 1
        )
        runCatching { debtRepository.update(updatedDebt) }.onSuccess {
            _uiState.update { it.copy(debt = updatedDebt, isLoading = false, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false, error = error.message ?: "Unknown error") }
        }
    }

    fun onMinusInstallment() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        if (_uiState.value.debt.paidInstallments <= 0) {
            _uiState.update { it.copy(error = "No installments paid yet") }
            return@launch
        }
        val updatedDebt = _uiState.value.debt.copy(
            paidInstallments = _uiState.value.debt.paidInstallments - 1
        )
        runCatching { debtRepository.update(updatedDebt) }.onSuccess {
            _uiState.update { it.copy(debt = updatedDebt, isLoading = false, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false, error = error.message ?: "Unknown error") }
        }
    }
}
