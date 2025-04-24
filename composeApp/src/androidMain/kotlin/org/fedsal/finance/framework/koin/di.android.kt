package org.fedsal.finance.framework.koin

import androidx.room.RoomDatabase
import org.fedsal.finance.framework.room.database.AppDatabase
import org.fedsal.finance.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder(get())
    }
}
