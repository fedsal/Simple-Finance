package org.fedsal.finance.ui.home.export

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.fedsal.finance.domain.models.AppColors
import org.fedsal.finance.domain.models.AppIcons
import org.fedsal.finance.domain.models.Debt
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.models.PaymentMethod
import org.fedsal.finance.domain.models.PaymentMethodType

/**
 * Tests unitarios para ExportViewModel.buildPrompt().
 *
 * Valida el contenido del prompt generado para IA en distintos escenarios:
 * estado vacío, deudas activas, filtrado de deudas pagadas/sin cuotas,
 * advertencias de presupuesto excedido, y ordenamiento cronológico.
 */
class ExportPromptBuilderTest {

    // ─── Helpers ────────────────────────────────────────────────────────────

    private fun creditMethod(id: Int = 1, name: String = "Visa") = PaymentMethod(
        id = id,
        name = name,
        type = PaymentMethodType.CREDIT,
        iconId = AppIcons.CARD.name,
        color = AppColors.CYAN.hexString,
    )

    private fun makeDebt(
        title: String,
        amount: Double,
        installments: Int,
        paidInstallments: Int,
        paymentMethod: PaymentMethod = creditMethod(),
    ) = Debt(
        id = 1,
        title = title,
        amount = amount,
        installments = installments,
        paidInstallments = paidInstallments,
        paymentMethod = paymentMethod,
    )

    private fun makeDebtBySource(
        paymentMethod: PaymentMethod = creditMethod(),
        debts: List<Debt>,
    ): DebtBySource {
        val totalDebt = debts.filter { it.installments > 0 }.sumOf { debt ->
            val installmentAmount = debt.amount / debt.installments
            debt.amount - (installmentAmount * debt.paidInstallments)
        }
        return DebtBySource(source = paymentMethod, debtsList = debts, totalDebt = totalDebt)
    }

    private fun buildPrompt(
        monthSummaries: List<ExportViewModel.MonthSummary> = emptyList(),
        debtsBySource: List<DebtBySource> = emptyList(),
        totalAmount: Double = 0.0,
        totalDebt: Double = 0.0,
        toPayNextMonth: Double = 0.0,
        totalExpenses: Int = 0,
    ) = ExportViewModel.buildPrompt(
        monthSummaries = monthSummaries,
        debtsBySource = debtsBySource,
        totalAmount = totalAmount,
        totalDebt = totalDebt,
        toPayNextMonth = toPayNextMonth,
        totalExpenses = totalExpenses,
    )

    // ─── Estado vacío ────────────────────────────────────────────────────────

    @Test
    fun buildPrompt_sinGastosNiDeudas_muestraMensajesVacios() {
        val prompt = buildPrompt()

        assertContains(prompt, "Sin gastos registrados.")
        assertContains(prompt, "Sin deudas activas.")
        assertFalse(prompt.contains("## Gastos por mes"))
        assertFalse(prompt.contains("## Deudas activas"))
    }

    @Test
    fun buildPrompt_siempreContieneSeccionSolicitud() {
        val prompt = buildPrompt()

        assertContains(prompt, "## Solicitud")
        assertContains(prompt, "1. Analiza la evolución de mis gastos mes a mes")
        assertContains(prompt, "3. Evalúa mis deudas y sugiere un orden de pago eficiente.")
    }

    @Test
    fun buildPrompt_totalGastosYMontoEnResumen() {
        val prompt = buildPrompt(totalExpenses = 5, totalAmount = 50_000.0)

        assertContains(prompt, "Total de gastos registrados: 5")
        assertContains(prompt, "50.000")
    }

    // ─── Sección de gastos ──────────────────────────────────────────────────

    @Test
    fun buildPrompt_conGastos_muestraSeccionPorMes() {
        val expense = ExportViewModel.ExpenseItem(
            title = "Netflix",
            amount = 5_000.0,
            paymentMethodName = "Débito",
        )
        val category = ExportViewModel.CategorySummary(
            name = "Entretenimiento",
            total = 5_000.0,
            budget = 0.0,
            expenses = listOf(expense),
        )
        val month = ExportViewModel.MonthSummary(
            year = 2026,
            month = 5,
            label = "Mayo 2026",
            totalAmount = 5_000.0,
            categories = listOf(category),
        )

        val prompt = buildPrompt(
            monthSummaries = listOf(month),
            totalExpenses = 1,
            totalAmount = 5_000.0,
        )

        assertContains(prompt, "## Gastos por mes")
        assertContains(prompt, "Mayo 2026")
        assertContains(prompt, "5.000")
        assertContains(prompt, "Entretenimiento")
        assertContains(prompt, "Netflix")
        assertContains(prompt, "Débito")
        assertFalse(prompt.contains("Sin gastos registrados."))
    }

    @Test
    fun buildPrompt_categoriaConPresupuesto_muestraPresupuesto() {
        val category = ExportViewModel.CategorySummary(
            name = "Supermercado",
            total = 20_000.0,
            budget = 30_000.0,
            expenses = emptyList(),
        )
        val month = ExportViewModel.MonthSummary(
            year = 2026, month = 4, label = "Abril 2026",
            totalAmount = 20_000.0, categories = listOf(category),
        )

        val prompt = buildPrompt(monthSummaries = listOf(month))

        assertContains(prompt, "presupuesto")
        assertContains(prompt, "30.000")
        assertFalse(prompt.contains("⚠️"))
    }

    @Test
    fun buildPrompt_categoriaExcedePresupuesto_muestraAdvertencia() {
        val category = ExportViewModel.CategorySummary(
            name = "Restaurantes",
            total = 40_000.0,
            budget = 20_000.0,
            expenses = emptyList(),
        )
        val month = ExportViewModel.MonthSummary(
            year = 2026, month = 5, label = "Mayo 2026",
            totalAmount = 40_000.0, categories = listOf(category),
        )

        val prompt = buildPrompt(monthSummaries = listOf(month))

        assertContains(prompt, "⚠️ excede presupuesto")
    }

    @Test
    fun buildPrompt_variossMeses_ordenadoMasRecientePrimero() {
        val makeMonth = { year: Int, month: Int, label: String ->
            ExportViewModel.MonthSummary(
                year = year, month = month, label = label,
                totalAmount = 10_000.0, categories = emptyList(),
            )
        }
        // Se pasan ya ordenados (como lo hace el ViewModel), el prompt los escribe en orden
        val months = listOf(
            makeMonth(2026, 5, "Mayo 2026"),
            makeMonth(2026, 4, "Abril 2026"),
            makeMonth(2026, 3, "Marzo 2026"),
        )

        val prompt = buildPrompt(monthSummaries = months)

        val idxMayo = prompt.indexOf("Mayo 2026")
        val idxAbril = prompt.indexOf("Abril 2026")
        val idxMarzo = prompt.indexOf("Marzo 2026")
        assertTrue(idxMayo < idxAbril, "Mayo debe aparecer antes que Abril")
        assertTrue(idxAbril < idxMarzo, "Abril debe aparecer antes que Marzo")
    }

    // ─── Sección de deudas ──────────────────────────────────────────────────

    @Test
    fun buildPrompt_deudaActiva_aparecerEnPrompt() {
        val debt = makeDebt(
            title = "MacBook",
            amount = 120_000.0,
            installments = 12,
            paidInstallments = 4,
        )
        val source = makeDebtBySource(debts = listOf(debt))

        val prompt = buildPrompt(
            debtsBySource = listOf(source),
            totalDebt = source.totalDebt,
            toPayNextMonth = 120_000.0 / 12,
        )

        assertContains(prompt, "## Deudas activas")
        assertContains(prompt, "MacBook")
        assertContains(prompt, "4/12 cuotas pagadas")
        assertContains(prompt, "10.000")          // cuota mensual = 120.000/12
        assertFalse(prompt.contains("Sin deudas activas."))
    }

    @Test
    fun buildPrompt_deudaPagada_noApareceEnSeccionActiva() {
        val paidDebt = makeDebt(
            title = "Heladera",
            amount = 60_000.0,
            installments = 6,
            paidInstallments = 6,   // completamente pagada
        )
        val unpaidDebt = makeDebt(
            title = "Notebook",
            amount = 24_000.0,
            installments = 12,
            paidInstallments = 3,
        )
        val source = makeDebtBySource(debts = listOf(paidDebt, unpaidDebt))

        val prompt = buildPrompt(
            debtsBySource = listOf(source),
            totalDebt = source.totalDebt,
        )

        assertFalse(prompt.contains("Heladera"), "La deuda pagada no debe aparecer en activas")
        assertContains(prompt, "Notebook")
    }

    @Test
    fun buildPrompt_deudaSinCuotas_noApareceEnSeccionActiva() {
        val zeroCuotasDebt = makeDebt(
            title = "Deuda sin cuotas",
            amount = 50_000.0,
            installments = 0,       // sin estructura de cuotas
            paidInstallments = 0,
        )
        val source = DebtBySource(
            source = creditMethod(),
            debtsList = listOf(zeroCuotasDebt),
            totalDebt = 0.0,        // las deudas con installments==0 no contribuyen al total
        )

        val prompt = buildPrompt(
            debtsBySource = listOf(source),
            totalDebt = 0.0,
        )

        // Con totalDebt == 0 la fuente queda incluida, pero la deuda individual es filtrada
        assertFalse(
            prompt.contains("Deuda sin cuotas"),
            "Deudas con installments=0 no deben aparecer en activas",
        )
    }

    @Test
    fun buildPrompt_variasDeudaEnMismaFuente_todasAparecen() {
        val debt1 = makeDebt("Celular", 12_000.0, installments = 6, paidInstallments = 1)
        val debt2 = makeDebt("Tablet", 18_000.0, installments = 9, paidInstallments = 0)
        val source = makeDebtBySource(debts = listOf(debt1, debt2))

        val prompt = buildPrompt(
            debtsBySource = listOf(source),
            totalDebt = source.totalDebt,
        )

        assertContains(prompt, "Celular")
        assertContains(prompt, "Tablet")
        assertContains(prompt, "1/6 cuotas pagadas")
        assertContains(prompt, "0/9 cuotas pagadas")
    }

    @Test
    fun buildPrompt_cuotaMensualCalculadaCorrectamente() {
        // 12.000 / 6 cuotas = 2.000 por mes
        val debt = makeDebt("Smart TV", amount = 12_000.0, installments = 6, paidInstallments = 2)
        val source = makeDebtBySource(debts = listOf(debt))

        val prompt = buildPrompt(
            debtsBySource = listOf(source),
            totalDebt = source.totalDebt,
        )

        assertContains(prompt, "2.000")   // cuota mensual formateada
        assertContains(prompt, "2/6 cuotas pagadas")
    }
}
