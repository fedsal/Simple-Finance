package org.fedsal.finance.ui.home.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.usecases.GetDebtBySourceUseCase
import org.fedsal.finance.ui.common.DateManager

class BalanceScreenViewModel(
    private val getDebtBySourceUseCase: GetDebtBySourceUseCase
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
        viewModelScope.launch {
            DateManager.selectedMonth.collectLatest { month ->
                fetchDebts(month)
            }
        }
    }

    private fun fetchDebts(month: Month) {
        getDebtBySourceUseCase.invoke(
            params = month,
            onError = {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message
                )
            },
            onSuccess = { debts ->
                viewModelScope.launch {
                    debts.collectLatest { debtList ->
                        val totalBalance = debtList.sumOf { it.totalDebt }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            debts = debtList,
                            totalBalance = totalBalance
                        )
                    }
                }
            }
        )
    }
}
