package ci.nsu.mobile.main.ui.calculations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class StepOneFragment : Fragment() {

    private val viewModel: CalculationFlowViewModel by navGraphViewModels(R.id.calculation_flow_graph) {
        appViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amountInput = view.findViewById<TextInputEditText>(R.id.etInitialAmount)
        val periodInput = view.findViewById<TextInputEditText>(R.id.etPeriodMonths)
        val nextButton = view.findViewById<Button>(R.id.btnNextStep)
        val resetButton = view.findViewById<Button>(R.id.btnResetFlow)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (amountInput.text.isNullOrEmpty()) {
                        amountInput.setText(state.initialAmount)
                    }
                    if (periodInput.text.isNullOrEmpty()) {
                        periodInput.setText(state.periodMonths)
                    }
                }
            }
        }

        nextButton.setOnClickListener {
            val amount = amountInput.text?.toString().orEmpty()
            val period = periodInput.text?.toString().orEmpty()

            if (amount.toDoubleOrNull() == null || amount.toDouble() <= 0.0) {
                Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (period.toIntOrNull() == null || period.toInt() <= 0) {
                Toast.makeText(requireContext(), "Enter a valid period", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.setStepOneData(amount, period)
            findNavController().navigate(R.id.action_stepOneFragment_to_stepTwoFragment)
        }

        resetButton.setOnClickListener {
            viewModel.reset()
            amountInput.setText("")
            periodInput.setText("")
        }
    }
}
