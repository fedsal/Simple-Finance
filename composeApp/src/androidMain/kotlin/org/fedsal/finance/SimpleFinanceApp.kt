package org.fedsal.finance

import android.app.Application
import org.fedsal.finance.framework.koin.initializeKoin
import org.koin.android.ext.koin.androidContext

class SimpleFinanceApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@SimpleFinanceApp)
        }
    }
}
