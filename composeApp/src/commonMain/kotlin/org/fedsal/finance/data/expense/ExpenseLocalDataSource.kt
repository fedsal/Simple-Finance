package org.fedsal.finance.data.expense

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Month
import org.fedsal.finance.domain.models.Expense

interface ExpenseLocalDataSource {
    suspend fun create(expense: Expense)
    suspend fun read(): List<Expense>
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)
    suspend fun getById(id: Long): Expense

    fun getExpensesByCategory(categoryId: Int, month: Month, year: Int): Flow<List<Expense>>
}
