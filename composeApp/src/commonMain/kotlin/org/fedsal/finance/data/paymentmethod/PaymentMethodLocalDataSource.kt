package org.fedsal.finance.data.paymentmethod

import org.fedsal.finance.domain.models.PaymentMethod

interface PaymentMethodLocalDataSource {
    suspend fun create(paymentMethod: PaymentMethod)
    suspend fun read(): List<PaymentMethod>
    suspend fun delete(paymentMethod: PaymentMethod)
}
