package ci.nsu.mobile.main.ui.calculations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.db.DepositCalculation
import ci.nsu.mobile.main.data.local.SessionManager
import ci.nsu.mobile.main.data.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculationFilter(
    val dateQuery: String = "",
    val minAmount: String = "",
    val maxAmount: String = "",
    val rateQuery: String = ""
)

class MyCalculationsViewModel(
    private val depositRepository: DepositRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val filters = MutableStateFlow(CalculationFilter())
    private val userId = sessionManager.userId

    val calculations: StateFlow<List<DepositCalculation>> = combine(
        userId?.let { depositRepository.getCalculationsForUser(it) } ?: flowOf(emptyList()),
        filters
    ) { items, filter ->
        items.filter { item -> matchesFilter(item, filter) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeFilter: StateFlow<CalculationFilter> = filters

    fun updateFilters(
        dateQuery: String,
        minAmount: String,
        maxAmount: String,
        rateQuery: String
    ) {
        filters.value = CalculationFilter(
            dateQuery = dateQuery.trim(),
            minAmount = minAmount.trim(),
            maxAmount = maxAmount.trim(),
            rateQuery = rateQuery.trim()
        )
    }

    fun deleteCalculation(calculation: DepositCalculation) {
        viewModelScope.launch {
            depositRepository.delete(calculation)
        }
    }

    private fun matchesFilter(
        calculation: DepositCalculation,
        filter: CalculationFilter
    ): Boolean {
        val dateText = formatter.format(Date(calculation.calculationDate))
        val minAmount = filter.minAmount.toDoubleOrNull()
        val maxAmount = filter.maxAmount.toDoubleOrNull()
        val rate = filter.rateQuery.toDoubleOrNull()

        return (filter.dateQuery.isBlank() || dateText.contains(filter.dateQuery, true)) &&
            (minAmount == null || calculation.initialAmount >= minAmount) &&
            (maxAmount == null || calculation.initialAmount <= maxAmount) &&
            (rate == null || calculation.interestRate == rate)
    }
}
