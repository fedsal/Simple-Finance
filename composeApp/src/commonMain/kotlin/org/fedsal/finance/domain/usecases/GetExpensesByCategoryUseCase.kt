package org.fedsal.finance.domain.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.ExpensesByCategory

class GetExpensesByCategoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : BaseUseCase<Month, Flow<List<ExpensesByCategory>>>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun execute(params: Month): Flow<List<ExpensesByCategory>> {
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        return categoryRepository.read() // Flow<List<Category>>
            .flatMapLatest { categories ->
                val flows: List<Flow<ExpensesByCategory>> = categories.map { category ->
                    expenseRepository
                        .getExpensesByCategory(category.id, params, currentDate.year)
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

                if (flows.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(flows) { it.toList() }
                }
            }
    }


}
