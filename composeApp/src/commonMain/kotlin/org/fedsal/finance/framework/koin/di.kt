package org.fedsal.finance.framework.koin

import org.fedsal.finance.data.debt.DebtLocalDataSource
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.expense.ExpenseLocalDataSource
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.framework.room.database.getCategoryDao
import org.fedsal.finance.framework.room.database.getDebtDao
import org.fedsal.finance.framework.room.database.getExpenseDao
import org.fedsal.finance.framework.room.database.getPaymentMethodDao
import org.fedsal.finance.framework.room.database.getRoomDatabase
import org.fedsal.finance.framework.room.datasource.DebtRoomDataSource
import org.fedsal.finance.framework.room.datasource.ExpenseRoomDataSource
import org.fedsal.finance.ui.expenses.ExpensesViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
) {
    // Start Koin
    startKoin {
        config?.invoke(this)
        modules(
            platformModule(),
            provideDataSourceModule,
            provideRepositoryModule,
            provideDatabaseModule
        )
    }
}

// Modules

expect fun platformModule(): Module

val provideDataSourceModule = module {
    singleOf(::ExpenseRoomDataSource).bind(ExpenseLocalDataSource::class)
    singleOf(::DebtRoomDataSource).bind(DebtLocalDataSource::class)
}

val provideRepositoryModule = module {
    singleOf(::ExpenseRepository)
    singleOf(::DebtRepository)
}

val provideDatabaseModule = module {
    single { getRoomDatabase(get()) }
    single { getExpenseDao(get()) }
    single { getDebtDao(get()) }
    single { getCategoryDao(get()) }
    single { getPaymentMethodDao(get()) }
}

val provideViewModelModule = module {
    single { ExpensesViewModel(get()) }
}
