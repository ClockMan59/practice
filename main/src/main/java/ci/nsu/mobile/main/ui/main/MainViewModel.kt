package ci.nsu.mobile.main.ui.main

import androidx.lifecycle.ViewModel
import ci.nsu.mobile.main.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _currentUserName = MutableStateFlow(
        sessionManager.userDisplayName ?: sessionManager.userLogin ?: "Пользователь"
    )
    val currentUserName: StateFlow<String> = _currentUserName

    fun logout() {
        sessionManager.clearSession()
    }
}
