package org.fedsal.finance.data.expense

import io.ktor.util.date.Month
import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.Expense

interface ExpenseLocalDataSource {
    suspend fun create(expense: Expense)
    suspend fun read(): List<Expense>
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)

    fun getExpensesByCategory(categoryId: Int, month: Month, year: Int): Flow<List<Expense>>
}
