package org.fedsal.finance.ui.common.composables.modals.selectcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.common.DateManager

class SelectCategoryViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase
) : ViewModel() {

    data class UIState(
        val categories: List<Pair<Double, Category>> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun initViewModel() {
        loadCategories()
    }

    private fun loadCategories() {
        _uiState.value = UIState(isLoading = true)
        runCatching {
            getExpensesByCategoryUseCase.invoke(
                params = GetExpensesByCategoryUseCase.Params(
                    DateManager.selectedMonth.value,
                    DateManager.selectedYear.value
                ),
                onError = { throw it },
                onSuccess = { categories ->
                    viewModelScope.launch {
                        categories.collect { expensesByCategories ->
                            _uiState.update { ui ->
                                ui.copy(isLoading = false, categories = expensesByCategories.map {
                                    it.totalSpent to it.category
                                })
                            }
                        }
                    }
                }
            )
        }.onFailure { ex ->
            ex.printStackTrace()
            _uiState.value = UIState(
                isLoading = false,
                error = ex.message ?: "An error occurred while loading categories"
            )
        }
    }
}
