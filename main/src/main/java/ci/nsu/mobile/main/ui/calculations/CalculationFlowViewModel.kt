package ci.nsu.mobile.main.ui.calculations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.db.DepositCalculation
import ci.nsu.mobile.main.data.local.SessionManager
import ci.nsu.mobile.main.data.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CalculationUiState(
    val initialAmount: String = "",
    val periodMonths: String = "",
    val monthlyTopUp: String = "",
    val selectedRate: Double? = null,
    val result: DepositCalculation? = null,
    val error: String? = null,
    val saved: Boolean = false
)

class CalculationFlowViewModel(
    private val depositRepository: DepositRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculationUiState())
    val uiState: StateFlow<CalculationUiState> = _uiState

    fun setStepOneData(initialAmount: String, periodMonths: String) {
        _uiState.value = _uiState.value.copy(
            initialAmount = initialAmount.trim(),
            periodMonths = periodMonths.trim(),
            error = null
        )
    }

    fun setStepTwoData(selectedRate: Double, monthlyTopUp: String) {
        _uiState.value = _uiState.value.copy(
            selectedRate = selectedRate,
            monthlyTopUp = monthlyTopUp.trim(),
            error = null
        )
    }

    fun getAvailableRates(): List<Double> {
        val months = _uiState.value.periodMonths.toIntOrNull() ?: return emptyList()
        return buildList {
            add(15.0)
            if (months > 6) {
                add(10.0)
            }
        }
    }

    fun calculate(): Boolean {
        val initialAmountValue = _uiState.value.initialAmount.toDoubleOrNull()
        val periodMonthsValue = _uiState.value.periodMonths.toIntOrNull()
        val selectedRateValue = _uiState.value.selectedRate
        val monthlyTopUpValue = _uiState.value.monthlyTopUp.toDoubleOrNull()
        val userIdValue = sessionManager.userId

        if (initialAmountValue == null || initialAmountValue <= 0.0) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid deposit amount")
            return false
        }

        if (periodMonthsValue == null || periodMonthsValue <= 0) {
            _uiState.value = _uiState.value.copy(error = "Deposit period must be greater than 0")
            return false
        }

        if (selectedRateValue == null) {
            _uiState.value = _uiState.value.copy(error = "Select an interest rate")
            return false
        }

        if (userIdValue == null) {
            _uiState.value = _uiState.value.copy(error = "Current user is not available")
            return false
        }

        val initialAmount = initialAmountValue ?: return false
        val periodMonths = periodMonthsValue ?: return false
        val selectedRate = selectedRateValue ?: return false
        val userId = userIdValue ?: return false

        var currentBalance = initialAmount
        var totalInvested = initialAmount
        val monthlyRate = selectedRate / 100.0 / 12.0

        repeat(periodMonths) {
            currentBalance += currentBalance * monthlyRate
            val topUpValue = monthlyTopUpValue ?: 0.0
            currentBalance += topUpValue
            totalInvested += topUpValue
        }

        val result = DepositCalculation(
            userId = userId,
            initialAmount = initialAmount,
            periodMonths = periodMonths,
            interestRate = selectedRate,
            monthlyTopUp = monthlyTopUpValue,
            finalAmount = currentBalance,
            interestEarned = currentBalance - totalInvested,
            calculationDate = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(result = result, error = null, saved = false)
        return true
    }

    fun saveCalculation() {
        val result = _uiState.value.result ?: return
        if (_uiState.value.saved) return

        viewModelScope.launch {
            depositRepository.insert(result)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun reset() {
        _uiState.value = CalculationUiState()
    }
}
