package ci.nsu.mobile.main.ui.calculations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import kotlinx.coroutines.launch
import java.util.Locale

class ResultFragment : Fragment() {

    private val viewModel: CalculationFlowViewModel by navGraphViewModels(R.id.calculation_flow_graph) {
        appViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialText = view.findViewById<TextView>(R.id.tvResInitial)
        val periodText = view.findViewById<TextView>(R.id.tvResPeriod)
        val rateText = view.findViewById<TextView>(R.id.tvResRate)
        val topUpText = view.findViewById<TextView>(R.id.tvResTopUp)
        val interestText = view.findViewById<TextView>(R.id.tvResInterest)
        val finalText = view.findViewById<TextView>(R.id.tvResFinal)
        val errorText = view.findViewById<TextView>(R.id.tvResultError)
        val saveButton = view.findViewById<Button>(R.id.btnSaveCalculation)
        val newButton = view.findViewById<Button>(R.id.btnStartNewCalculation)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val result = state.result ?: return@collect
                    initialText.text = "Initial amount: ${formatMoney(result.initialAmount)}"
                    periodText.text = "Period: ${result.periodMonths} months"
                    rateText.text = "Rate: ${result.interestRate}%"
                    topUpText.text = "Monthly top up: ${formatMoney(result.monthlyTopUp ?: 0.0)}"
                    interestText.text = "Interest earned: ${formatMoney(result.interestEarned)}"
                    finalText.text = "Final amount: ${formatMoney(result.finalAmount)}"
                    errorText.isVisible = !state.error.isNullOrBlank()
                    errorText.text = state.error
                    saveButton.isEnabled = !state.saved
                }
            }
        }

        saveButton.setOnClickListener {
            viewModel.saveCalculation()
            saveButton.isEnabled = false
        }

        newButton.setOnClickListener {
            viewModel.reset()
            findNavController().popBackStack(R.id.stepOneFragment, false)
        }
    }

    private fun formatMoney(amount: Double): String {
        return String.format(Locale.getDefault(), "%.2f RUB", amount)
    }
}
