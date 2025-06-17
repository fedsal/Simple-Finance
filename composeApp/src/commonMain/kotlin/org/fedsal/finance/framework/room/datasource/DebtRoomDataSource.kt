package org.fedsal.finance.framework.room.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun getDebtById(debtId: Long): Debt? {
        return debtDao.getDebtById(debtId)?.toDomain()
    }

    override fun getDebtsByPaymentMethod(paymentMethodId: Int): Flow<List<Debt>> {
        return debtDao.getDebtsByPaymentMethod(paymentMethodId)
            .map { debtEntities -> debtEntities.map { it.toDomain() } }
    }
}
