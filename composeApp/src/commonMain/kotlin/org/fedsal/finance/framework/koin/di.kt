package org.fedsal.finance.framework.koin

import org.fedsal.finance.data.expense.ExpenseLocalDataSource
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.framework.room.database.getExpenseDao
import org.fedsal.finance.framework.room.database.getRoomDatabase
import org.fedsal.finance.framework.room.datasource.ExpenseRoomDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun platformModule(): Module

val provideDataSourceModule = module {
    singleOf(::ExpenseRoomDataSource).bind(ExpenseLocalDataSource::class)
}

val provideRepositoryModule = module {
    singleOf(::ExpenseRepository)
}

val provideDatabaseModule = module {
    single { getRoomDatabase(get()) }
    single { getExpenseDao(get()) }
}
