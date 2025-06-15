package org.fedsal.finance.framework.room.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
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

@Database(entities = [ExpenseEntity::class, DebtEntity::class, CategoryEntity::class, PaymentMethodEntity::class], version = 2)
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
        .addMigrations(MIGRATION_1_2)
        .build()
    INSTANCE.database = db
    return db
}

fun getExpenseDao(database: AppDatabase) = database.expenseDao()

fun getDebtDao(database: AppDatabase) = database.debtDao()

fun getPaymentMethodDao(database: AppDatabase) = database.paymentMethodDao()

fun getCategoryDao(database: AppDatabase) = database.categoryDao()


// MIGRATIONS
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        // Drop the old debts table
        connection.execSQL("DROP TABLE IF EXISTS debts")

        // Recreate with new schema
        connection.execSQL("""
            CREATE TABLE debts (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                categoryId INTEGER NOT NULL,
                installments INTEGER NOT NULL,
                paidInstallments INTEGER NOT NULL,
                paymentMethodId INTEGER NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent())
    }
}
