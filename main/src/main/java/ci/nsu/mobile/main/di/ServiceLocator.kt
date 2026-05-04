package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.db.AppDatabase
import ci.nsu.mobile.main.data.local.SessionManager
import ci.nsu.mobile.main.data.network.AppClientFactory
import ci.nsu.mobile.main.data.repository.AuthRepository
import ci.nsu.mobile.main.data.repository.AuthRepositoryImpl
import ci.nsu.mobile.main.data.repository.DepositRepository
import ci.nsu.mobile.main.data.repository.DepositRepositoryImpl
import ci.nsu.mobile.main.data.repository.MockAuthRepository

class ServiceLocator(context: Context) {
    private val appContext = context.applicationContext

    val sessionManager: SessionManager by lazy {
        SessionManager(appContext)
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(appContext)
    }

    val authRepository: AuthRepository by lazy {
//        AuthRepositoryImpl(
//            primaryApiService = AppClientFactory.create(sessionManager),
//            secondaryApiService = AppClientFactory.createAlternate(sessionManager),
//            sessionManager = sessionManager
//        )
        MockAuthRepository(sessionManager)
    }

    val depositRepository: DepositRepository by lazy {
        DepositRepositoryImpl(database.depositDao())
    }

    val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(this)
    }
}
