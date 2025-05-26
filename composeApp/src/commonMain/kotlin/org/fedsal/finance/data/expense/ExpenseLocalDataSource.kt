package org.fedsal.finance.data.expense

import io.ktor.util.date.Month
import org.fedsal.finance.domain.models.Expense

interface ExpenseLocalDataSource {
    suspend fun create(expense: Expense)
    suspend fun read(): List<Expense>
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)

    suspend fun getExpensesByCategory(categoryId: Int, month: Month, year: Int): List<Expense>
}
