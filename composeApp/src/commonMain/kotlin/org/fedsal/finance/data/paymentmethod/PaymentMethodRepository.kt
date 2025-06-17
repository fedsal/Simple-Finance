package org.fedsal.finance.data.paymentmethod

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.PaymentMethod

class PaymentMethodRepository(private val dataSource: PaymentMethodLocalDataSource) {
    suspend fun create(paymentMethod: PaymentMethod) = dataSource.create(paymentMethod)
    suspend fun read(): List<PaymentMethod> = dataSource.read()
    suspend fun delete(paymentMethod: PaymentMethod) = dataSource.delete(paymentMethod)

    fun getById(paymentMethodId: Int): Flow<PaymentMethod?> {
        return dataSource.getById(paymentMethodId)
    }
}
