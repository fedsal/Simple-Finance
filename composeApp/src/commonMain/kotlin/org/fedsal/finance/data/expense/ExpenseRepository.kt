package org.fedsal.finance.data.expense

import io.ktor.util.date.Month
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.Expense

class ExpenseRepository(
    private val expenseLocalDataSource: ExpenseLocalDataSource
) {
    suspend fun createExpense(expense: Expense) = expenseLocalDataSource.create(expense)
    suspend fun getAllExpenses() = expenseLocalDataSource.read()
    suspend fun updateExpense(expense: Expense) = expenseLocalDataSource.update(expense)
    suspend fun deleteExpense(expense: Expense) = expenseLocalDataSource.delete(expense)

    fun getExpensesByCategory(categoryId: Int, month: Month, year: Int): Flow<List<Expense>> {
        return expenseLocalDataSource.getExpensesByCategory(categoryId, month, year)
    }
}
