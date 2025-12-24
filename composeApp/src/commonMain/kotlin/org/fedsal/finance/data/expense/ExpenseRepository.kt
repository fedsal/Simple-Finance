package org.fedsal.finance.data.expense

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Month
import org.fedsal.finance.data.debt.DebtLocalDataSource
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.Expense
import org.fedsal.finance.domain.models.PaymentMethodType

class ExpenseRepository(
    private val expenseLocalDataSource: ExpenseLocalDataSource,
    private val debtLocalDataSource: DebtLocalDataSource
) {
    suspend fun createExpense(expense: Expense) {
       val id = expenseLocalDataSource.create(expense)
        if (expense.paymentMethod.type == PaymentMethodType.CREDIT || expense.paymentMethod.type == PaymentMethodType.LOAN) {
            val debt = Debt(
                title = expense.title,
                amount = expense.amount,
                date = expense.date,
                paymentMethod = expense.paymentMethod,
                description = expense.description,
                category = expense.category.copy(id = expense.category.userCategoryId),
                installments = 1,
                expenseId = id
            )
            debtLocalDataSource.create(debt)
        }
    }
    fun getAllExpenses() = expenseLocalDataSource.read()
    suspend fun updateExpense(expense: Expense) = expenseLocalDataSource.update(expense)
    suspend fun deleteExpense(expense: Expense) {
        expenseLocalDataSource.delete(expense)

        if (expense.paymentMethod.type == PaymentMethodType.CREDIT || expense.paymentMethod.type == PaymentMethodType.LOAN) {
            debtLocalDataSource.deleteByExpenseId(expense.id.toLong())
        }
    }
    suspend fun getExpenseById(id: Long) = expenseLocalDataSource.getById(id)

    fun getExpensesByCategory(categoryId: Int, month: Month, year: Int): Flow<List<Expense>> {
        return expenseLocalDataSource.getExpensesByCategory(categoryId, month, year)
    }
}
