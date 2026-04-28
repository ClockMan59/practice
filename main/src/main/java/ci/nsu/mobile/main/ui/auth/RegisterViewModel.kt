package ci.nsu.mobile.main.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.model.GroupDto
import ci.nsu.mobile.main.data.model.PersonDto
import ci.nsu.mobile.main.data.model.RegisterRequest
import ci.nsu.mobile.main.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val groups: List<GroupDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.getGroups()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(groups = it, isLoading = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        middleName: String,
        birthDate: String,
        gender: String,
        groupId: Int,
        login: String,
        password: String,
        email: String,
        phoneNumber: String
    ) {
        val validationError = validate(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            gender = gender,
            groupId = groupId,
            login = login,
            password = password,
            email = email,
            phoneNumber = phoneNumber
        )

        if (validationError != null) {
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        val request = RegisterRequest(
            login = login.trim(),
            password = password,
            email = email.trim(),
            phoneNumber = phoneNumber.trim(),
            roleId = 1,
            authAllowed = true,
            person = PersonDto(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                middleName = middleName.trim(),
                birthDate = birthDate.trim(),
                gender = gender,
                groupId = groupId
            )
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.register(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun consumeSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    private fun validate(
        firstName: String,
        lastName: String,
        birthDate: String,
        gender: String,
        groupId: Int,
        login: String,
        password: String,
        email: String,
        phoneNumber: String
    ): String? {
        if (firstName.isBlank() || lastName.isBlank()) {
            return "Имя и фамилия обязательны"
        }
        if (!birthDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return "Дата рождения должна быть в формате YYYY-MM-DD"
        }
        if (gender.isBlank()) {
            return "Выберите пол"
        }
        if (groupId <= 0) {
            return "Выберите группу"
        }
        if (login.isBlank() || password.length < 4) {
            return "Введите логин и пароль не короче 4 символов"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Введите корректный email"
        }
        if (phoneNumber.isBlank()) {
            return "Введите номер телефона"
        }
        return null
    }
}
