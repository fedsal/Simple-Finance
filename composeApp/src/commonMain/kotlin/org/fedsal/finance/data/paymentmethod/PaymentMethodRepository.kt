package org.fedsal.finance.data.paymentmethod

import org.fedsal.finance.domain.models.PaymentMethod

class PaymentMethodRepository(private val dataSource: PaymentMethodLocalDataSource) {
    suspend fun create(paymentMethod: PaymentMethod) = dataSource.create(paymentMethod)
    suspend fun read(): List<PaymentMethod> = dataSource.read()
    suspend fun delete(paymentMethod: PaymentMethod) = dataSource.delete(paymentMethod)
}
