package org.fedsal.finance.domain.usecases

import io.ktor.util.date.Month
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.ExpensesByCategory

class GetExpensesByCategoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : BaseUseCase<Month, Flow<List<ExpensesByCategory>>>() {

    override suspend fun execute(params: Month): Flow<List<ExpensesByCategory>> {
        val categories = categoryRepository.read() // List<Category>

        val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        // Lista de Flow<ExpensesByCategory>
        val flows: List<Flow<ExpensesByCategory>> = categories.map { category ->
            expenseRepository
                .getExpensesByCategory(category.id, params, date.year) // Flow<List<ExpenseEntity>>
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
