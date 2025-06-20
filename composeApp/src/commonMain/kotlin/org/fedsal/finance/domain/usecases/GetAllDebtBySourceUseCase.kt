package org.fedsal.finance.domain.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.models.PaymentMethodType

class GetAllDebtBySourceUseCase(
    private val debtRepository: DebtRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : BaseUseCase<GetAllDebtBySourceUseCase.Params, Flow<List<DebtBySource>>>() {
    data object Params

    override suspend fun execute(params: Params): Flow<List<DebtBySource>> {
        val paymentMethods = paymentMethodRepository.read()
            .filter { it.type == PaymentMethodType.CREDIT || it.type == PaymentMethodType.LOAN }

        val flows = paymentMethods.map { paymentMethod ->
            debtRepository.getDebtsByPaymentMethod(
                paymentMethod.id,
            ).map { debts ->
                val totalDebt = debts.sumOf { debt ->
                    val installmentImport = debt.amount/debt.installments
                    debt.amount - (installmentImport * debt.paidInstallments)
                }

                DebtBySource(
                    source = paymentMethod,
                    debtsList = debts,
                    totalDebt = totalDebt
                )
            }
        }

        return combine(flows) { debtBySources ->
            debtBySources.toList()
        }
    }
}
