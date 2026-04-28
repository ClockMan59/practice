package ci.nsu.mobile.main.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.model.UserDto
import ci.nsu.mobile.main.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<UserDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UsersViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState(isLoading = true))
    val uiState: StateFlow<UsersUiState> = _uiState

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.getUsers()
                .onSuccess {
                    _uiState.value = UsersUiState(users = it, isLoading = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
