package ci.nsu.mobile.main.ui.common

import androidx.fragment.app.Fragment
import ci.nsu.mobile.main.DepositApplication
import ci.nsu.mobile.main.di.AppViewModelFactory

fun Fragment.appViewModelFactory(): AppViewModelFactory {
    return (requireActivity().application as DepositApplication).serviceLocator.viewModelFactory
}
