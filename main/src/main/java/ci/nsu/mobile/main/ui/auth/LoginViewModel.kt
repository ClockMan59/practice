package ci.nsu.mobile.main.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(error = "Введите логин и пароль")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            authRepository.login(login.trim(), password)
                .onSuccess { _uiState.value = LoginUiState(isSuccess = true) }
                .onFailure { _uiState.value = LoginUiState(error = it.message) }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
