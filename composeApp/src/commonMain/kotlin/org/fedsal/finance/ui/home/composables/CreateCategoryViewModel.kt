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

class CreateCategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateCategoryUIState())
    val uiState: StateFlow<CreateCategoryUIState>
        get() = _uiState

    fun createCategory(title: String, budget: Double, color: AppColors, icon: AppIcons) =
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

    data class CreateCategoryUIState(
        var isLoading: Boolean = false,
        var shouldContinue: Boolean = false,
        var categoryId: Long = -1,
        var error: String? = null
    )
}
