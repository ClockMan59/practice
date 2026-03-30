package ci.nsu.moble.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CounterUiState(
    val count: Int = 0,
    val history: List<String> = emptyList()
)

class CounterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun increment() {
        _uiState.update { it.copy(
            count = it.count + 1,
            history = (listOf("+1 (итого: ${it.count + 1})") + it.history).take(5)
        )}
    }

    fun decrement() {
        _uiState.update { it.copy(
            count = it.count - 1,
            history = (listOf("-1 (итого: ${it.count - 1})") + it.history).take(5)
        )}
    }

    fun reset() {
        _uiState.update { it.copy(
            count = 0,
            history = (listOf("Сброс") + it.history).take(5)
        )}
    }
}