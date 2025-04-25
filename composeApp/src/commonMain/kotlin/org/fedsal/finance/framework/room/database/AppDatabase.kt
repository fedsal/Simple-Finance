package org.fedsal.finance.framework.room.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.fedsal.finance.framework.room.dao.DebtDao
import org.fedsal.finance.framework.room.dao.ExpenseDao
import org.fedsal.finance.framework.room.model.DebtEntity
import org.fedsal.finance.framework.room.model.ExpenseEntity

@Database(entities = [ExpenseEntity::class, DebtEntity::class], version = 1)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun debtDao(): DebtDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getExpenseDao(database: AppDatabase) = database.expenseDao()

fun getDebtDao(database: AppDatabase) = database.debtDao()
