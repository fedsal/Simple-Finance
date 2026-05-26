package org.fedsal.finance.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.fedsal.finance.data.paymentmethod.PaymentMethodLocalDataSource
import org.fedsal.finance.domain.models.PaymentMethod

class FakePaymentMethodLocalDataSource(
    initialMethods: List<PaymentMethod> = emptyList(),
) : PaymentMethodLocalDataSource {

    private var methods = initialMethods
    private val _flow = MutableStateFlow(initialMethods)

    override suspend fun create(paymentMethod: PaymentMethod) {
        methods = methods + paymentMethod
        _flow.value = methods
    }

    override suspend fun read(): List<PaymentMethod> = methods

    override fun readWithFlow(): Flow<List<PaymentMethod>> = _flow

    override suspend fun delete(paymentMethod: PaymentMethod) {
        methods = methods.filterNot { it.id == paymentMethod.id }
        _flow.value = methods
    }

    override suspend fun update(paymentMethod: PaymentMethod) {
        methods = methods.map { if (it.id == paymentMethod.id) paymentMethod else it }
        _flow.value = methods
    }

    override fun getById(paymentMethodId: Int): Flow<PaymentMethod?> =
        flowOf(methods.find { it.id == paymentMethodId })
}
