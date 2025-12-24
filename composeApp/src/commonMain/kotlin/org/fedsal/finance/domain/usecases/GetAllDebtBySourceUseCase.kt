package org.fedsal.finance.domain.usecases

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.models.PaymentMethodType

class GetAllDebtBySourceUseCase(
    private val debtRepository: DebtRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) : BaseUseCase<GetAllDebtBySourceUseCase.Params, Flow<List<DebtBySource>>>() {
    data object Params

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun execute(params: Params): Flow<List<DebtBySource>> {
        return paymentMethodRepository.readWithFlow().flatMapLatest { paymentMethodList ->
            // Fix color
            paymentMethodList.forEach { paymentMethod ->
                if ((paymentMethodList-paymentMethod).firstOrNull { it.color == paymentMethod.color } != null) {
                    val currentColors =
                        paymentMethodRepository.read().map { AppColors.fromHex(it.color) }
                    val unusedColor = AppColors.entries.firstOrNull { it !in currentColors }
                    paymentMethodRepository.update(
                        paymentMethod.copy(
                            color = unusedColor?.hexString ?: ""
                        )
                    )
                }
            }
            // Filter payment methods by type and get debts
            val paymentMethods = paymentMethodList
                .filter { it.type == PaymentMethodType.CREDIT || it.type == PaymentMethodType.LOAN }
            val flows = paymentMethods.map { paymentMethod ->
                debtRepository.getDebtsByPaymentMethod(
                    paymentMethod.id,
                ).map { debts ->
                    val totalDebt = debts.filter { it.installments > 0 }.sumOf { debt ->
                        val installmentImport = debt.amount / debt.installments
                        debt.amount - (installmentImport * debt.paidInstallments)
                    }

                    DebtBySource(
                        source = paymentMethod,
                        debtsList = debts,
                        totalDebt = totalDebt
                    )
                }
            }


            if (flows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(flows) { it.toList() }
            }
        }
    }
}
