package org.fedsal.finance.framework.room.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.fedsal.finance.domain.models.Category
import org.fedsal.finance.domain.models.DefaultCategories
import org.fedsal.finance.domain.models.DefaultPaymentMethods
import org.fedsal.finance.framework.room.dao.CategoryDao
import org.fedsal.finance.framework.room.dao.DebtDao
import org.fedsal.finance.framework.room.dao.ExpenseDao
import org.fedsal.finance.framework.room.dao.PaymentMethodDao
import org.fedsal.finance.framework.room.model.CategoryEntity
import org.fedsal.finance.framework.room.model.DebtEntity
import org.fedsal.finance.framework.room.model.ExpenseEntity
import org.fedsal.finance.framework.room.model.PaymentMethodEntity
import org.fedsal.finance.framework.room.model.toEntity

object INSTANCE {
    var database: AppDatabase? = null
}

@Database(entities = [ExpenseEntity::class, DebtEntity::class, CategoryEntity::class, PaymentMethodEntity::class], version = 1)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun debtDao(): DebtDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun categoryDao(): CategoryDao
}

private class InitialDataCallback: RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        CoroutineScope(Dispatchers.IO).launch {
            INSTANCE.database?.let { db ->
                db.categoryDao().insertAll(
                    DefaultCategories.MARKET.toEntity(),
                    DefaultCategories.FIXED_EXPENSES.toEntity(),
                    DefaultCategories.TRANSPORT.toEntity(),
                    DefaultCategories.RENT.toEntity(),
                    DefaultCategories.FUN.toEntity()
                )
                db.paymentMethodDao().insertAll(
                    DefaultPaymentMethods.CASH.toEntity(),
                    DefaultPaymentMethods.CREDIT_CARD.toEntity(),
                )
            }
        }
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    val db = builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(InitialDataCallback())
        .build()
    INSTANCE.database = db
    return db
}

fun getExpenseDao(database: AppDatabase) = database.expenseDao()

fun getDebtDao(database: AppDatabase) = database.debtDao()

fun getPaymentMethodDao(database: AppDatabase) = database.paymentMethodDao()

fun getCategoryDao(database: AppDatabase) = database.categoryDao()
