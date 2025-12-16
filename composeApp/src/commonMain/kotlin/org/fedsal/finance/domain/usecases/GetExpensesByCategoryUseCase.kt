package org.fedsal.finance.domain.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Month
import kotlinx.datetime.number
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.ExpensesByCategory
import org.fedsal.finance.ui.common.DateManager

class GetExpensesByCategoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : BaseUseCase<GetExpensesByCategoryUseCase.Params, Flow<List<ExpensesByCategory>>>() {

    data class Params(val month: Month, val year: Int)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun execute(params: Params): Flow<List<ExpensesByCategory>> {
        val currentDateString = DateManager.getCurrentDate()

        val selectedMonth = params.month.number.toString().padStart(2, '0')
        val selectedDateString = "$selectedMonth/${params.year}"

        return categoryRepository.read(selectedDate = selectedDateString, currentDate = currentDateString) // Flow<List<Category>>
            .filter { it.all { category -> category.date == selectedDateString } }.flatMapLatest { categories ->
                val flows: List<Flow<ExpensesByCategory>> = categories.map { category ->
                    expenseRepository
                        .getExpensesByCategory(category.id, params.month, params.year)
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
