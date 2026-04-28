package ci.nsu.mobile.main.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.mobile.main.ui.auth.LoginViewModel
import ci.nsu.mobile.main.ui.auth.RegisterViewModel
import ci.nsu.mobile.main.ui.calculations.CalculationFlowViewModel
import ci.nsu.mobile.main.ui.calculations.MyCalculationsViewModel
import ci.nsu.mobile.main.ui.main.MainViewModel
import ci.nsu.mobile.main.ui.users.UsersViewModel

class AppViewModelFactory(
    private val serviceLocator: ServiceLocator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(serviceLocator.authRepository) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(serviceLocator.authRepository) as T

            modelClass.isAssignableFrom(UsersViewModel::class.java) ->
                UsersViewModel(serviceLocator.authRepository) as T

            modelClass.isAssignableFrom(MyCalculationsViewModel::class.java) ->
                MyCalculationsViewModel(
                    serviceLocator.depositRepository,
                    serviceLocator.sessionManager
                ) as T

            modelClass.isAssignableFrom(CalculationFlowViewModel::class.java) ->
                CalculationFlowViewModel(
                    serviceLocator.depositRepository,
                    serviceLocator.sessionManager
                ) as T

            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(serviceLocator.sessionManager) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
