package ci.nsu.mobile.main

import android.app.Application
import ci.nsu.mobile.main.di.ServiceLocator

class DepositApplication : Application() {
    val serviceLocator: ServiceLocator by lazy { ServiceLocator(this) }
}
