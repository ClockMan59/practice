package mobile.presentation

import mobile.data.DepositCalculation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobile.domain.DepositRepository
import kotlin.math.pow

class DepositViewModel(private val repository: DepositRepository) : ViewModel() {

    // Стейты для первого экрана ввода
    private val _initialAmount = MutableStateFlow("")
    val initialAmount = _initialAmount.asStateFlow()

    private val _periodMonths = MutableStateFlow("")
    val periodMonths = _periodMonths.asStateFlow()

    // Стейты для второго экрана ввода
    private val _monthlyTopUp = MutableStateFlow("")
    val monthlyTopUp = _monthlyTopUp.asStateFlow()

    // Результаты расчета
    private val _calculationResult = MutableStateFlow<DepositCalculation?>(null)
    val calculationResult = _calculationResult.asStateFlow()

    val history = repository.allCalculations

    fun setInitialAmount(amount: String) { _initialAmount.value = amount }
    fun setPeriodMonths(months: String) { _periodMonths.value = months }
    fun setMonthlyTopUp(amount: String) { _monthlyTopUp.value = amount }

    fun getAvailableInterestRate(): Double {
        val months = _periodMonths.value.toIntOrNull() ?: return 0.0
        return when {
            months < 6 -> 15.0
            months in 6..11 -> 10.0
            else -> 5.0
        }
    }

    fun calculate() {
        val p = _initialAmount.value.toDoubleOrNull() ?: 0.0
        val n = _periodMonths.value.toIntOrNull() ?: 0
        val pmt = _monthlyTopUp.value.toDoubleOrNull() ?: 0.0
        val rate = getAvailableInterestRate()

        if (p <= 0 || n <= 0) return

        val rm = rate / 12 / 100
        val finalAmount = if (rm > 0) {
            p * (1 + rm).pow(n) + pmt * (((1 + rm).pow(n) - 1) / rm)
        } else {
            p + (pmt * n)
        }

        val totalInvested = p + (pmt * n)
        val interestEarned = finalAmount - totalInvested

        _calculationResult.value = DepositCalculation(
            initialAmount = p,
            periodMonths = n,
            interestRate = rate,
            monthlyTopUp = pmt,
            finalAmount = finalAmount,
            interestEarned = interestEarned,
            calculationDate = System.currentTimeMillis()
        )
    }

    fun saveCalculation() {
        _calculationResult.value?.let { calc ->
            viewModelScope.launch {
                repository.insert(calc)
            }
        }
    }

    fun clearData() {
        _initialAmount.value = ""
        _periodMonths.value = ""
        _monthlyTopUp.value = ""
        _calculationResult.value = null
    }
}