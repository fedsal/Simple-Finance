package org.fedsal.finance.framework.room.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.fedsal.finance.data.paymentmethod.PaymentMethodLocalDataSource
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.framework.room.dao.PaymentMethodDao
import org.fedsal.finance.framework.room.model.toDomain
import org.fedsal.finance.framework.room.model.toEntity

class PaymentMethodRoomDataSource(
    private val paymentMethodDao: PaymentMethodDao
): PaymentMethodLocalDataSource {
    override suspend fun create(paymentMethod: PaymentMethod) {
        return paymentMethodDao.create(paymentMethod.toEntity())
    }

    override suspend fun read(): List<PaymentMethod> {
        return paymentMethodDao.readAll().map { it.toDomain() }
    }

    override fun readWithFlow(): Flow<List<PaymentMethod>> {
        return paymentMethodDao.readAllWithFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun delete(paymentMethod: PaymentMethod) {
        return paymentMethodDao.delete(paymentMethod.toEntity())
    }

    override suspend fun update(paymentMethod: PaymentMethod) {
        return paymentMethodDao.update(paymentMethod.toEntity())
    }

    override fun getById(paymentMethodId: Int): Flow<PaymentMethod?> {
        return paymentMethodDao.getPaymentMethodById(paymentMethodId).map { it?.toDomain() }
    }
}
