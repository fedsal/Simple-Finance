package org.fedsal.finance

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.fedsal.finance.framework.room.database.AppDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase>  {
    val dbFile = context.getDatabasePath("simple_finance.db")
    return Room.databaseBuilder<AppDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
}
