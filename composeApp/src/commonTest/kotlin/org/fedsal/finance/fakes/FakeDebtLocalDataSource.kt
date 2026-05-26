package org.fedsal.finance.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.fedsal.finance.data.debt.DebtLocalDataSource
import org.fedsal.finance.domain.models.Debt

class FakeDebtLocalDataSource(
    private val debtsByPaymentMethodId: Map<Int, List<Debt>> = emptyMap(),
) : DebtLocalDataSource {

    override suspend fun create(debt: Debt) = Unit

    override suspend fun getAllDebts(): List<Debt> =
        debtsByPaymentMethodId.values.flatten()

    override suspend fun update(debt: Debt) = Unit

    override suspend fun delete(debt: Debt) = Unit

    override suspend fun getDebtById(debtId: Long): Debt? = null

    override fun getDebtsByPaymentMethod(paymentMethodId: Int): Flow<List<Debt>> =
        flowOf(debtsByPaymentMethodId[paymentMethodId] ?: emptyList())

    override suspend fun deleteByExpenseId(expenseId: Long) = Unit
}
