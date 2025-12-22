package org.fedsal.finance.framework.koin

import org.fedsal.finance.data.category.CategoryLocalDataSource
import org.fedsal.finance.data.category.CategoryRepository
import org.fedsal.finance.data.debt.DebtLocalDataSource
import org.fedsal.finance.data.debt.DebtRepository
import org.fedsal.finance.data.expense.ExpenseLocalDataSource
import org.fedsal.finance.data.expense.ExpenseRepository
import org.fedsal.finance.data.paymentmethod.PaymentMethodLocalDataSource
import org.fedsal.finance.data.paymentmethod.PaymentMethodRepository
import org.fedsal.finance.data.usercategory.UserCategoryLocalDataSource
import org.fedsal.finance.domain.usecases.GetAllDebtBySourceUseCase
import org.fedsal.finance.domain.usecases.GetExpensesByCategoryUseCase
import org.fedsal.finance.framework.room.database.getCategoryDao
import org.fedsal.finance.framework.room.database.getDebtDao
import org.fedsal.finance.framework.room.database.getExpenseDao
import org.fedsal.finance.framework.room.database.getPaymentMethodDao
import org.fedsal.finance.framework.room.database.getRoomDatabase
import org.fedsal.finance.framework.room.database.getUserCategoryDao
import org.fedsal.finance.framework.room.datasource.CategoryRoomDataSource
import org.fedsal.finance.framework.room.datasource.DebtRoomDataSource
import org.fedsal.finance.framework.room.datasource.ExpenseRoomDataSource
import org.fedsal.finance.framework.room.datasource.PaymentMethodRoomDataSource
import org.fedsal.finance.framework.room.datasource.UserCategoryRoomDataSource
import org.fedsal.finance.ui.categoryExpenses.ExpensesByCategoryViewModel
import org.fedsal.finance.ui.common.composables.modals.categorydata.CategoryDataViewModel
import org.fedsal.finance.ui.common.composables.modals.debtdata.DebtDataViewModel
import org.fedsal.finance.ui.common.composables.modals.expenseinfo.ExpenseInfoModalViewModel
import org.fedsal.finance.ui.common.composables.modals.paymentmethod.CreatePaymentMethodViewModel
import org.fedsal.finance.ui.common.composables.modals.selectcategory.SelectCategoryViewModel
import org.fedsal.finance.ui.debtdetail.DebtDetailViewModel
import org.fedsal.finance.ui.debtdetail.modal.DebtDetailModalViewModel
import org.fedsal.finance.ui.home.allcategories.ExpensesViewModel
import org.fedsal.finance.ui.home.balance.BalanceViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
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
            provideDatabaseModule,
            useCaseModule,
            provideViewModelModule
        )
    }
}

// Modules

expect fun platformModule(): Module

val provideDataSourceModule = module {
    singleOf(::ExpenseRoomDataSource).bind(ExpenseLocalDataSource::class)
    singleOf(::DebtRoomDataSource).bind(DebtLocalDataSource::class)
    singleOf(::CategoryRoomDataSource).bind(CategoryLocalDataSource::class)
    singleOf(::PaymentMethodRoomDataSource).bind(PaymentMethodLocalDataSource::class)
    singleOf(::UserCategoryRoomDataSource).bind(UserCategoryLocalDataSource::class)
}

val provideRepositoryModule = module {
    singleOf(::ExpenseRepository)
    singleOf(::DebtRepository)
    singleOf(::CategoryRepository)
    singleOf(::PaymentMethodRepository)
}

val provideDatabaseModule = module {
    single { getRoomDatabase(get()) }
    single { getExpenseDao(get()) }
    single { getDebtDao(get()) }
    single { getCategoryDao(get()) }
    single { getPaymentMethodDao(get()) }
    single { getUserCategoryDao(get()) }
}

val useCaseModule = module {
    single { GetExpensesByCategoryUseCase(get(), get()) }
    single { GetAllDebtBySourceUseCase(get(), get()) }
}

val provideViewModelModule = module {
    viewModel { ExpensesViewModel(get(), get(), get()) }
    viewModel { ExpensesByCategoryViewModel(get(), get(), get()) }
    viewModel { ExpenseInfoModalViewModel(get(), get(), get()) }
    viewModel { SelectCategoryViewModel(get(), get(), get()) }
    viewModel { CategoryDataViewModel(get()) }
    viewModel { BalanceViewModel(get()) }
    viewModel { DebtDataViewModel(get(), get()) }
    viewModel { DebtDetailViewModel(get(), get(), get()) }
    viewModel { DebtDetailModalViewModel(get()) }
    viewModel { CreatePaymentMethodViewModel(get()) }
}
