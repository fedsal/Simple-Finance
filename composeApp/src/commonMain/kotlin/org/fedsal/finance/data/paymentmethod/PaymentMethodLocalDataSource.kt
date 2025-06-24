package org.fedsal.finance.data.paymentmethod

import kotlinx.coroutines.flow.Flow
import org.fedsal.finance.domain.models.PaymentMethod

interface PaymentMethodLocalDataSource {
    suspend fun create(paymentMethod: PaymentMethod)
    suspend fun read(): List<PaymentMethod>
    fun readWithFlow(): Flow<List<PaymentMethod>>
    suspend fun delete(paymentMethod: PaymentMethod)
    fun getById(paymentMethodId: Int): Flow<PaymentMethod?>
}
