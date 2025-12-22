package org.fedsal.finance.ui.common.composables.modals.selectcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.toCategory
import org.fedsal.finance.domain.usecases.GetAllDebtBySourceUseCase
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.ui.common.DateManager

class SelectCategoryViewModel(
    private val getExpensesByCategoryUseCase: GetExpensesByCategoryUseCase,
    private val getDebtBySourceUseCase: GetAllDebtBySourceUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    data class UIState(
        val categories: List<Pair<Double, Category>> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> get() = _uiState

    fun initViewModel(isOnBalance: Boolean) {
        if (isOnBalance) {
            loadDebtCategories()
        } else {
            loadCategories()
        }
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

    private fun loadDebtCategories() {
        _uiState.value = UIState(isLoading = true)
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
                        val debtItems = debtList
                            .flatMap { it.debtsList }
                            .groupBy { it.category }
                            .map { (category, debts) ->
                                debts.sumOf { it.amount } to category
                            }.toMutableList()
                        val categories = categoryRepository.readUserCategories()
                        val categoriesInDebtItems = debtItems.map { it.second }
                        val categoriesNotInDebtItems = categories.filterNot {
                            categoriesInDebtItems.any { debtCat ->
                                it.id == debtCat.userCategoryId || it.id == debtCat.id
                            }
                        }
                        debtItems.forEachIndexed { index, pair ->
                            val category = categories.find { it.id == pair.second.id }
                            category?.let {
                                debtItems[index] = pair.first to it.toCategory().copy(id = it.id)
                            } ?: run {
                                //debtItems.removeAt(index)
                            }
                        }
                        debtItems.addAll(categoriesNotInDebtItems.map {
                            0.0 to it.toCategory().copy(id = it.id)
                        })
                        _uiState.update { ui ->
                            ui.copy(isLoading = false, categories = debtItems)
                        }
                    }
                }
            }
        )
    }

    fun dispose() {
        getDebtBySourceUseCase.cancel()
    }
}
