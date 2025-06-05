package org.fedsal.finance.ui.home.composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.Category

class CategoryDataViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateCategoryUIState())
    val uiState: StateFlow<CreateCategoryUIState>
        get() = _uiState
    private lateinit var mode: DisplayInfoMode
    private var categoryId: Long = -1


    fun initViewModel(mode: DisplayInfoMode, categoryId: Long) {
        this.mode= mode
        this.categoryId = categoryId

        if (mode == DisplayInfoMode.EDIT) loadCategory(categoryId)
    }

    private fun loadCategory(id: Long) = viewModelScope.launch {
        runCatching {
            categoryRepository.getById(id.toInt())
        }.onSuccess { category ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    category = category ?: Category(),
                    error = null
                )
            }
        }.onFailure { e ->
            e.printStackTrace()
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun execute(title: String, budget: Double, color: AppColors, icon: AppIcons) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        when (mode) {
            DisplayInfoMode.CREATE -> createCategory(title, budget, color, icon)
            DisplayInfoMode.EDIT -> editCategory(categoryId, title, budget, color, icon)
        }
    }

    private fun createCategory(title: String, budget: Double, color: AppColors, icon: AppIcons) =
        viewModelScope.launch {
            runCatching {
                val category = Category(
                    title = title,
                    budget = budget,
                    color = color.hexString,
                    iconId = icon.name
                )
                categoryRepository.create(category).takeIf { it > 0 }?.let { categoryId ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            shouldContinue = true,
                            categoryId = categoryId
                        )
                    }
                } ?: throw Exception("Failed to create category")
            }.onFailure { e ->
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    private fun editCategory(
        categoryId: Long,
        title: String,
        budget: Double,
        color: AppColors,
        icon: AppIcons
    ) = viewModelScope.launch {
        runCatching {
            val category = Category(
                id = categoryId.toInt(),
                title = title,
                budget = budget,
                color = color.hexString,
                iconId = icon.name
            )
            categoryRepository.update(category)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    shouldContinue = true,
                    categoryId = categoryId
                )
            }
        }.onFailure { e ->
            e.printStackTrace()
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    data class CreateCategoryUIState(
        var isLoading: Boolean = false,
        var shouldContinue: Boolean = false,
        var categoryId: Long = -1,
        var category: Category = Category(),
        var error: String? = null
    )
}
