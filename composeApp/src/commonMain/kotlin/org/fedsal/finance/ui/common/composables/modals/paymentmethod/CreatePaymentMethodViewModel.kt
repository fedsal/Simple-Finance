package org.fedsal.finance.ui.common.composables.modals.paymentmethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType

class CreatePaymentMethodViewModel(
    private val paymentMethodRepository: PaymentMethodRepository
) : ViewModel() {
    data class UIState(
        val shouldContinue: Boolean = false,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun create(name: String, paymentMethodType: PaymentMethodType, iconId: AppIcons, ) = viewModelScope.launch {
        runCatching {
            val currentColors = paymentMethodRepository.read().map { AppColors.fromHex(it.color) }
            val unusedColor = AppColors.entries.firstOrNull { it !in currentColors }
            paymentMethodRepository.create(
                PaymentMethod(
                    name = name,
                    type = paymentMethodType,
                    iconId = iconId.name,
                    color = unusedColor?.hexString ?: ""
                )
            )
        }.onSuccess {
            _uiState.update { it.copy(shouldContinue = true, error = null) }
        }.onFailure { exception ->
            _uiState.update { it.copy(shouldContinue = false, error = exception.message ?: "Unknown error") }
        }
    }

    fun resetState() {
        _uiState.value = UIState()
    }
}
