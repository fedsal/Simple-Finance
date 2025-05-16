package org.fedsal.finance.data.expense

import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.ExpensesByCategory

class ExpenseRepository(
    private val expenseLocalDataSource: ExpenseLocalDataSource
) {
    suspend fun createExpense(expense: Expense) = expenseLocalDataSource.create(expense)
    suspend fun getAllExpenses() = expenseLocalDataSource.read()
    suspend fun updateExpense(expense: Expense) = expenseLocalDataSource.update(expense)
    suspend fun deleteExpense(expense: Expense) = expenseLocalDataSource.delete(expense)

    suspend fun getExpensesByCategory(categoryId: String): ExpensesByCategory {
        val expenses = expenseLocalDataSource.getExpensesByCategory(categoryId)
        val totalSpent = expenses.sumOf { it.amount }
        val availableAmount = expenses.first().category.budget - totalSpent
        return ExpensesByCategory(
            category = expenses.first().category,
            expensesList = expenses,
            totalSpent = totalSpent,
            availableAmount = availableAmount
        )
    }

    suspend fun getAllExpensesByCategory(): List<ExpensesByCategory> {
        return expenseLocalDataSource.read().groupBy { it.category }
            .map { (category, expenses) ->
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
}
