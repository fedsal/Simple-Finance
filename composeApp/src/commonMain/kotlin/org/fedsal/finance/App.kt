package org.fedsal.finance

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            val repository = koinInject<DebtRepository>()
            LaunchedEffect(Unit) {
                val expense = Debt(
                    title = "Groceries",
                    amount = 50.0,
                    date = "2023-10-01",
                    description = "Weekly groceries",
                    category = Category(
                        id = 1,
                        title = "Food",
                        iconId = "1",
                        budget = 100.0,
                        color = "#FF0000",
                    ),
                    paymentMethod = PaymentMethod(
                        id = 1,
                        name = "Credit Card",
                        iconId = "1",
                        color = "#00FF00",
                        type = PaymentMethodType.CASH
                    ),
                    installments = 3
                )
                repository.createDebt(expense)
                val expenses = repository.getAllDebts()
                expenses.forEach {
                    println("Debt: ${it.title}, Amount: ${it.amount}")
                }
            }
        }
    }
}
