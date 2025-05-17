package org.fedsal.finance.domain.usecases

import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.ExpensesByCategory

class GetExpensesByCategoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
): BaseUseCase<GetExpensesByCategoryUseCase.Params, List<ExpensesByCategory>>() {
    data object Params
    override suspend fun execute(params: Params): List<ExpensesByCategory> {
        val expenses = categoryRepository.read()
        return expenses.map { category ->
            val expensesByCategory = expenseRepository.getExpensesByCategory(category.id.toString())
            ExpensesByCategory(
                category = category,
                expensesList = expensesByCategory,
                totalSpent = expensesByCategory.sumOf { it.amount },
                availableAmount = category.budget - expensesByCategory.sumOf { it.amount }
            )
        }
    }
}
