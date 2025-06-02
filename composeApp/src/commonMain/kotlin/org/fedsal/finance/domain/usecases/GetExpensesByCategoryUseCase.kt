package org.fedsal.finance.domain.usecases

import io.ktor.util.date.Month
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.ExpensesByCategory

class GetExpensesByCategoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : BaseUseCase<GetExpensesByCategoryUseCase.Params, Flow<List<ExpensesByCategory>>>() {

    data class Params(
        val month: Month,
        val year: Int
    )

    override suspend fun execute(params: Params): Flow<List<ExpensesByCategory>> {
        val categories = categoryRepository.read() // List<Category>

        // Lista de Flow<ExpensesByCategory>
        val flows: List<Flow<ExpensesByCategory>> = categories.map { category ->
            expenseRepository
                .getExpensesByCategory(category.id, params.month, params.year) // Flow<List<ExpenseEntity>>
                .map { expenses ->
                    val totalSpent = expenses.sumOf { it.amount }
                    val availableAmount = category.budget - totalSpent

                    ExpensesByCategory(
                        category = category,
                        expensesList = expenses,
                        totalSpent = totalSpent,
                        availableAmount = availableAmount
                    )
                }
        }

        return combine(flows) { expensesByCategoryArray ->
            expensesByCategoryArray.toList()
        }
    }

}
