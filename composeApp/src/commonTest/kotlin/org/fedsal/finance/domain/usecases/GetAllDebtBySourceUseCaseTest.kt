package org.fedsal.finance.domain.usecases

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType
import org.fedsal.finance.fakes.FakeDebtLocalDataSource
import org.fedsal.finance.fakes.FakePaymentMethodLocalDataSource

/**
 * Tests unitarios para GetAllDebtBySourceUseCase.
 *
 * Valida el filtrado de métodos de pago por tipo, el cálculo de totalDebt,
 * y la construcción de la lista de DebtBySource para cada fuente.
 */
class GetAllDebtBySourceUseCaseTest {

    // ─── Helpers ────────────────────────────────────────────────────────────

    private fun creditMethod(
        id: Int,
        name: String = "Visa",
        color: String = AppColors.CYAN.hexString,
    ) = PaymentMethod(
        id = id, name = name, type = PaymentMethodType.CREDIT,
        iconId = AppIcons.CARD.name, color = color,
    )

    private fun loanMethod(
        id: Int,
        name: String = "Préstamo",
        color: String = AppColors.ORANGE.hexString,
    ) = PaymentMethod(
        id = id, name = name, type = PaymentMethodType.LOAN,
        iconId = AppIcons.BANK.name, color = color,
    )

    private fun cashMethod(id: Int) = PaymentMethod(
        id = id, name = "Efectivo", type = PaymentMethodType.CASH,
        iconId = AppIcons.CASH.name, color = AppColors.GREEN.hexString,
    )

    private fun makeDebt(
        id: Int,
        title: String,
        amount: Double,
        installments: Int,
        paidInstallments: Int,
        paymentMethodId: Int,
    ) = Debt(
        id = id,
        title = title,
        amount = amount,
        installments = installments,
        paidInstallments = paidInstallments,
        paymentMethod = PaymentMethod(id = paymentMethodId),
    )

    private fun buildUseCase(
        paymentMethods: List<PaymentMethod>,
        debtsByPaymentMethodId: Map<Int, List<Debt>> = emptyMap(),
    ): GetAllDebtBySourceUseCase {
        val pmDataSource = FakePaymentMethodLocalDataSource(paymentMethods)
        val debtDataSource = FakeDebtLocalDataSource(debtsByPaymentMethodId)
        return GetAllDebtBySourceUseCase(
            debtRepository = DebtRepository(debtDataSource),
            paymentMethodRepository = PaymentMethodRepository(pmDataSource),
        )
    }

    // ─── Filtrado de métodos de pago ─────────────────────────────────────────

    @Test
    fun execute_metodoCash_noSeProcesa() = runTest {
        val useCase = buildUseCase(
            paymentMethods = listOf(cashMethod(id = 1)),
            debtsByPaymentMethodId = mapOf(1 to listOf(makeDebt(1, "Deuda", 1000.0, 1, 0, 1))),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertTrue(result.isEmpty(), "Los métodos CASH no deben generar DebtBySource")
    }

    @Test
    fun execute_sinMetodosCreditoNiPrestamo_retornaListaVacia() = runTest {
        val useCase = buildUseCase(paymentMethods = emptyList())

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun execute_soloIncluyeCreditYLoan() = runTest {
        val credit = creditMethod(id = 1, name = "MasterCard")
        val loan = loanMethod(id = 2, name = "Préstamo BNA")
        val cash = cashMethod(id = 3)

        val useCase = buildUseCase(
            paymentMethods = listOf(credit, loan, cash),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        val sourceNames = result.map { it.source.name }
        assertTrue(sourceNames.contains("MasterCard"))
        assertTrue(sourceNames.contains("Préstamo BNA"))
        assertFalse(sourceNames.contains("Efectivo"), "CASH no debe aparecer en el resultado")
    }

    // ─── Cálculo de totalDebt ────────────────────────────────────────────────

    @Test
    fun execute_sinCuotasPagadas_totalDebtEsElMontoCompleto() = runTest {
        val method = creditMethod(id = 1)
        val debt = makeDebt(
            id = 1, title = "Notebook", amount = 60_000.0,
            installments = 6, paidInstallments = 0, paymentMethodId = 1,
        )

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debt)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(1, result.size)
        assertEquals(60_000.0, result[0].totalDebt, "Sin cuotas pagadas, totalDebt == amount")
    }

    @Test
    fun execute_conCuotasParcialmentePagadas_totalDebtReducido() = runTest {
        // 60.000 / 6 = 10.000 por cuota; pagadas 2 → restante = 60.000 - 20.000 = 40.000
        val method = creditMethod(id = 1)
        val debt = makeDebt(
            id = 1, title = "Smart TV", amount = 60_000.0,
            installments = 6, paidInstallments = 2, paymentMethodId = 1,
        )

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debt)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(40_000.0, result[0].totalDebt)
    }

    @Test
    fun execute_deudaTotalmentePagada_totalDebtEsCero() = runTest {
        val method = creditMethod(id = 1)
        val debt = makeDebt(
            id = 1, title = "Celular", amount = 12_000.0,
            installments = 3, paidInstallments = 3,  // completamente pagada
            paymentMethodId = 1,
        )

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debt)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(0.0, result[0].totalDebt)
    }

    @Test
    fun execute_deudaSinCuotas_noContribuyeATotalDebt() = runTest {
        val method = creditMethod(id = 1)
        val debtSinCuotas = makeDebt(
            id = 1, title = "Deuda libre", amount = 50_000.0,
            installments = 0,   // sin estructura de cuotas
            paidInstallments = 0, paymentMethodId = 1,
        )

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debtSinCuotas)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(0.0, result[0].totalDebt, "installments=0 no debe contribuir al totalDebt")
    }

    // ─── Lista de deudas por fuente ──────────────────────────────────────────

    @Test
    fun execute_variasDeudas_todasIncluidasEnDebtsList() = runTest {
        val method = creditMethod(id = 1)
        val debt1 = makeDebt(1, "Heladera", 30_000.0, 6, 0, paymentMethodId = 1)
        val debt2 = makeDebt(2, "Lavarropas", 20_000.0, 4, 1, paymentMethodId = 1)

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debt1, debt2)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(2, result[0].debtsList.size)
        val titles = result[0].debtsList.map { it.title }
        assertTrue(titles.contains("Heladera"))
        assertTrue(titles.contains("Lavarropas"))
    }

    @Test
    fun execute_variosMetodos_cadaUnoTieneSusPropiosDeudas() = runTest {
        val credit = creditMethod(id = 1, name = "Visa")
        val loan = loanMethod(id = 2, name = "BNA", color = AppColors.PURPLE.hexString)
        val debtCredit = makeDebt(1, "MacBook", 100_000.0, 12, 0, paymentMethodId = 1)
        val debtLoan = makeDebt(2, "Moto", 80_000.0, 24, 6, paymentMethodId = 2)

        val useCase = buildUseCase(
            paymentMethods = listOf(credit, loan),
            debtsByPaymentMethodId = mapOf(1 to listOf(debtCredit), 2 to listOf(debtLoan)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(2, result.size)
        val visaSource = result.first { it.source.name == "Visa" }
        val bnaSource = result.first { it.source.name == "BNA" }
        assertEquals(1, visaSource.debtsList.size)
        assertEquals("MacBook", visaSource.debtsList[0].title)
        assertEquals(1, bnaSource.debtsList.size)
        assertEquals("Moto", bnaSource.debtsList[0].title)
    }

    @Test
    fun execute_totalDebtSumaVariasDeudas() = runTest {
        // Deuda 1: 12.000 / 6 = 2.000/cuota; 2 pagadas → resta 8.000
        // Deuda 2: 6.000 / 3 = 2.000/cuota; 0 pagadas → resta 6.000
        // Total esperado: 14.000
        val method = loanMethod(id = 1)
        val debt1 = makeDebt(1, "A", 12_000.0, installments = 6, paidInstallments = 2, paymentMethodId = 1)
        val debt2 = makeDebt(2, "B", 6_000.0, installments = 3, paidInstallments = 0, paymentMethodId = 1)

        val useCase = buildUseCase(
            paymentMethods = listOf(method),
            debtsByPaymentMethodId = mapOf(1 to listOf(debt1, debt2)),
        )

        val result = useCase.execute(GetAllDebtBySourceUseCase.Params).first()

        assertEquals(14_000.0, result[0].totalDebt)
    }
}
