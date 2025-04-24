package org.fedsal.finance

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fedsal.finance.framework.room.database.AppDatabase

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("simple_finance.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
