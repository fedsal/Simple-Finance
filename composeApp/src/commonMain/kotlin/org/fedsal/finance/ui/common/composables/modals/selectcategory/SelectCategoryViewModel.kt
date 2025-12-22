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
                        val repoCategories = categoryRepository.readUserCategories()
                        val repoCategoriesMap = repoCategories.associateBy { it.id }

                        val processedDebts = debtList
                            .flatMap { it.debtsList }
                            .groupBy { it.category }
                            .map { (category, debts) ->
                                debts.sumOf { it.amount } to category
                            }
                            .mapNotNull { (amount, tempCategory) ->
                                val realCategory = repoCategoriesMap[tempCategory.id]

                                realCategory?.let {
                                    amount to it.toCategory().copy(id = it.id)
                                }
                            }

                        val usedCategoryIds = processedDebts.map { it.second.id }.toSet()

                        val unusedCategories = repoCategories
                            .filter { it.id !in usedCategoryIds }
                            .map {
                                0.0 to it.toCategory().copy(id = it.id)
                            }

                        _uiState.update { ui ->
                            ui.copy(isLoading = false, categories = processedDebts + unusedCategories)
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
