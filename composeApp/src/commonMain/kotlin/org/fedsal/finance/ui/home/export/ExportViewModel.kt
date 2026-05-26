package org.fedsal.finance.ui.home.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.domain.models.DebtBySource
import org.fedsal.finance.domain.usecases.GetAllDebtBySourceUseCase
import org.fedsal.finance.ui.common.formatDecimal

class ExportViewModel(
    private val expenseRepository: ExpenseRepository,
    private val getDebtBySourceUseCase: GetAllDebtBySourceUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    data class ExpenseItem(
        val title: String,
        val amount: Double,
        val paymentMethodName: String,
    )

    data class CategorySummary(
        val name: String,
        val total: Double,
        val budget: Double,
        val expenses: List<ExpenseItem>,
    )

    data class MonthSummary(
        val year: Int,
        val month: Int,
        val label: String,
        val totalAmount: Double,
        val categories: List<CategorySummary>,
    )

    data class ExportUiState(
        val totalExpenses: Int = 0,
        val totalAmount: Double = 0.0,
        val monthSummaries: List<MonthSummary> = emptyList(),
        val debtsBySource: List<DebtBySource> = emptyList(),
        val totalDebt: Double = 0.0,
        val toPayNextMonth: Double = 0.0,
        val prompt: String = "",
        val isLoading: Boolean = true,
    )

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> get() = _uiState

    fun initViewModel() {
        observeData()
    }

    private fun observeData() {
        getDebtBySourceUseCase.invoke(
            params = GetAllDebtBySourceUseCase.Params,
            onError = { _uiState.value = _uiState.value.copy(isLoading = false) },
            onSuccess = { debtFlow ->
                viewModelScope.launch {
                    combine(
                        expenseRepository.getAllExpenses(),
                        debtFlow
                    ) { expenses, debtsBySource ->
                        expenses to debtsBySource
                    }.collectLatest { (expenses, debtsBySource) ->
                        // Resolve full category data (toDomain() leaves title/budget empty)
                        val categoryCache = mutableMapOf<Int, String>()
                        val budgetCache = mutableMapOf<Int, Double>()
                        expenses.forEach { expense ->
                            val catId = expense.category.id
                            if (catId !in categoryCache) {
                                val fullCat = categoryRepository.getById(catId)
                                categoryCache[catId] = fullCat?.title.orEmpty().ifEmpty { "Sin categoría" }
                                budgetCache[catId] = fullCat?.budget ?: 0.0
                            }
                        }

                        // Group by year+month (most recent first), then by category within each month
                        val monthSummaries = expenses
                            .groupBy { extractYearMonth(it.date) }
                            .entries
                            .sortedByDescending { (ym, _) -> ym.first * 100 + ym.second }
                            .map { (yearMonth, monthExpenses) ->
                                val (year, month) = yearMonth
                                val categories = monthExpenses
                                    .groupBy { categoryCache[it.category.id] ?: "Sin categoría" }
                                    .map { (catName, catExpenses) ->
                                        val catId = catExpenses.first().category.id
                                        CategorySummary(
                                            name = catName,
                                            total = catExpenses.sumOf { it.amount },
                                            budget = budgetCache[catId] ?: 0.0,
                                            expenses = catExpenses
                                                .sortedByDescending { it.amount }
                                                .map { e ->
                                                    ExpenseItem(
                                                        title = e.title,
                                                        amount = e.amount,
                                                        paymentMethodName = e.paymentMethod.name
                                                            .ifEmpty { e.paymentMethod.type.name },
                                                    )
                                                },
                                        )
                                    }
                                    .sortedByDescending { it.total }

                                MonthSummary(
                                    year = year,
                                    month = month,
                                    label = "${MONTH_NAMES_ES[month] ?: month} $year",
                                    totalAmount = monthExpenses.sumOf { it.amount },
                                    categories = categories,
                                )
                            }

                        val totalAmount = expenses.sumOf { it.amount }
                        val filteredDebts = debtsBySource.filter { it.totalDebt > 0 }
                        val totalDebt = filteredDebts.sumOf { it.totalDebt }
                        val toPayNextMonth = debtsBySource.sumOf { source ->
                            source.debtsList
                                .filter { it.installments > 0 && it.paidInstallments < it.installments }
                                .sumOf { it.amount / it.installments }
                        }

                        _uiState.value = ExportUiState(
                            totalExpenses = expenses.size,
                            totalAmount = totalAmount,
                            monthSummaries = monthSummaries,
                            debtsBySource = filteredDebts,
                            totalDebt = totalDebt,
                            toPayNextMonth = toPayNextMonth,
                            prompt = buildPrompt(
                                monthSummaries = monthSummaries,
                                debtsBySource = filteredDebts,
                                totalAmount = totalAmount,
                                totalDebt = totalDebt,
                                toPayNextMonth = toPayNextMonth,
                                totalExpenses = expenses.size,
                            ),
                            isLoading = false,
                        )
                    }
                }
            }
        )
    }

    private fun extractYearMonth(isoDate: String): Pair<Int, Int> =
        Companion.extractYearMonth(isoDate)

    companion object {
        internal val MONTH_NAMES_ES = mapOf(
            1 to "Enero", 2 to "Febrero", 3 to "Marzo", 4 to "Abril",
            5 to "Mayo", 6 to "Junio", 7 to "Julio", 8 to "Agosto",
            9 to "Septiembre", 10 to "Octubre", 11 to "Noviembre", 12 to "Diciembre"
        )

        internal fun extractYearMonth(isoDate: String): Pair<Int, Int> {
            return try {
                val datePart = isoDate.substringBefore('T')
                val parts = datePart.split("-")
                parts[0].toInt() to parts[1].toInt()
            } catch (e: Exception) {
                0 to 0
            }
        }

        internal fun buildPrompt(
            monthSummaries: List<MonthSummary>,
            debtsBySource: List<DebtBySource>,
            totalAmount: Double,
            totalDebt: Double,
            toPayNextMonth: Double,
            totalExpenses: Int,
        ): String = buildString {
            appendLine("Analiza mi situación financiera personal y dame recomendaciones detalladas.")
            appendLine()
            appendLine("## Resumen general")
            appendLine("- Total de gastos registrados: $totalExpenses")
            appendLine("- Monto total gastado (todos los meses): \$${totalAmount.formatDecimal()}")
            appendLine()
            if (monthSummaries.isEmpty()) {
                appendLine("## Gastos")
                appendLine("- Sin gastos registrados.")
            } else {
                appendLine("## Gastos por mes")
                monthSummaries.forEach { month ->
                    appendLine()
                    appendLine("### ${month.label} — Total: \$${month.totalAmount.formatDecimal()}")
                    month.categories.forEach { cat ->
                        val budgetInfo = if (cat.budget > 0) " · presupuesto \$${cat.budget.formatDecimal()}" else ""
                        val overBudget = if (cat.budget > 0 && cat.total > cat.budget) " ⚠️ excede presupuesto" else ""
                        appendLine("**${cat.name}** — Total \$${cat.total.formatDecimal()}$budgetInfo$overBudget")
                        cat.expenses.forEach { expense ->
                            appendLine("  - ${expense.title}: \$${expense.amount.formatDecimal()} (Método de pago: ${expense.paymentMethodName})")
                        }
                    }
                }
            }
            appendLine()
            if (debtsBySource.isNotEmpty()) {
                appendLine("## Deudas activas")
                appendLine("- Deuda total pendiente: \$${totalDebt.formatDecimal()}")
                appendLine("- Cuotas a pagar el próximo mes: \$${toPayNextMonth.formatDecimal()}")
                appendLine()
                debtsBySource.forEach { source ->
                    appendLine("### ${source.source.name} — deuda: \$${source.totalDebt.formatDecimal()}")
                    val activeDebts = source.debtsList
                        .filter { it.installments > 0 && it.paidInstallments < it.installments }
                    if (activeDebts.isEmpty()) {
                        appendLine("  - Sin cuotas pendientes.")
                    } else {
                        activeDebts.forEach { debt ->
                            val monthly = debt.amount / debt.installments
                            appendLine(
                                "  - ${debt.title}: \$${debt.amount.formatDecimal()} total, " +
                                    "cuota \$${monthly.formatDecimal()}/mes, " +
                                    "${debt.paidInstallments}/${debt.installments} cuotas pagadas"
                            )
                        }
                    }
                }
                appendLine()
            } else {
                appendLine("## Deudas")
                appendLine("- Sin deudas activas.")
                appendLine()
            }
            appendLine("## Solicitud")
            appendLine("Por favor:")
            appendLine("1. Analiza la evolución de mis gastos mes a mes e identifica tendencias.")
            appendLine("2. Identifica categorías donde gasto de más o que exceden el presupuesto.")
            appendLine("3. Evalúa mis deudas y sugiere un orden de pago eficiente.")
            appendLine("4. Dame recomendaciones concretas para mejorar mi salud financiera.")
            appendLine("5. Señala cualquier patrón preocupante que detectes.")
        }
    }
}
