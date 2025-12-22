package org.fedsal.finance.ui.debtdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.toCategory
import kotlin.coroutines.CoroutineContext

class DebtDetailViewModel(
    private val debtRepository: DebtRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    data class UIState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val debts: List<Debt> = emptyList(),
        val totalDebt: Double = 0.0,
        val source: PaymentMethod = PaymentMethod()
    )

    private lateinit var ioDispatchers: CoroutineContext

    private var _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun init(sourceId: Int) {
        ioDispatchers = SupervisorJob() + Dispatchers.IO
        getPaymentMethod(sourceId)
        loadDebts(sourceId)
    }

    private fun getPaymentMethod(sourceId: Int) = viewModelScope.launch(ioDispatchers) {
        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            paymentMethodRepository.getById(sourceId).collectLatest { paymentMethod ->
                _uiState.update {
                    it.copy(
                        source = paymentMethod
                            ?: throw IllegalArgumentException("Payment method not found")
                    )
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

    private fun loadDebts(sourceId: Int) = viewModelScope.launch(ioDispatchers) {
        _uiState.update { it.copy(isLoading = true) }

        runCatching {
            debtRepository.getDebtsByPaymentMethod(sourceId).map { debt ->
                debt.map { item ->
                    item.copy(
                        category = categoryRepository.getUserCategoryById(item.category.id)
                            ?.toCategory()?.copy(id = item.category.id)
                            ?: item.category,
                    )
                }
            }.collectLatest { debts ->
                _uiState.update {
                    UIState(
                        isLoading = false,
                        debts = debts,
                        totalDebt = debts.sumOf { item ->
                            val installmentImport = item.amount / item.installments
                            item.amount - (installmentImport * item.paidInstallments)
                        },
                        source = it.source
                    )
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

    fun deleteDebt(debt: Debt) = viewModelScope.launch(ioDispatchers) {
        runCatching {
            debtRepository.delete(debt)
        }.onSuccess {
            _uiState.update { it.copy(debts = it.debts - debt) }
        }.onFailure { error ->
            error.printStackTrace()
            _uiState.update {
                it.copy(
                    error = error.message ?: "Failed to delete debt"
                )
            }
        }
    }

    fun dispose() {
        ioDispatchers.cancel()
        _uiState.value = UIState() // Reset state
    }
}
