package org.fedsal.finance.ui.home.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.usecases.GetAllDebtBySourceUseCase

class BalanceViewModel(
    private val getDebtBySourceUseCase: GetAllDebtBySourceUseCase
) : ViewModel() {
    data class BalanceUiState(
        val totalBalance: Double = 0.0,
        val debts: List<DebtBySource> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> get() = _uiState

    fun initViewModel() {
        fetchDebts()
    }

    private fun fetchDebts() {
        getDebtBySourceUseCase.invoke(
            params = GetAllDebtBySourceUseCase.Params,
            onError = {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            },
            onSuccess = { debts ->
                viewModelScope.launch {
                    debts.collectLatest { debtList ->
                        val filteredDebt = debtList.filter { it.totalDebt > 0 }
                        val totalBalance = filteredDebt.sumOf { it.totalDebt }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            debts = filteredDebt,
                            totalBalance = totalBalance
                        )
                    }
                }
            }
        )
    }
}
