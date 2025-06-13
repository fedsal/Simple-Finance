package org.fedsal.finance.framework.room.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Month
import org.fedsal.finance.data.debt.DebtLocalDataSource
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.framework.room.dao.DebtDao
import org.fedsal.finance.framework.room.model.toDomain
import org.fedsal.finance.framework.room.model.toEntity

class DebtRoomDataSource(
    private val debtDao: DebtDao
) : DebtLocalDataSource {
    override suspend fun create(debt: Debt) {
        debtDao.create(debt.toEntity())
    }

    override suspend fun getAllDebts(): List<Debt> {
        return debtDao.readAll().map { it.toDomain() }
    }

    override suspend fun update(debt: Debt) {
        debtDao.update(debt.toEntity())
    }

    override suspend fun delete(debt: Debt) {
        debtDao.delete(debt.toEntity())
    }

    override suspend fun getDebtsByPaymentMethod(
        paymentMethodId: Int,
        month: Month,
        year: Int
    ): Flow<List<Debt>> {
        val paddedMonth = (month.ordinal + 1).toString().padStart(2, '0')
        return debtDao.getDebtsByPaymentMethod(paymentMethodId, paddedMonth, year.toString())
            .map { debtEntities -> debtEntities.map { it.toDomain() } }
    }
}
