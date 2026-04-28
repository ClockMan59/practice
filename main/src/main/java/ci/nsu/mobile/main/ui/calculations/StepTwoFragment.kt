package ci.nsu.mobile.main.ui.calculations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
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

class StepTwoFragment : Fragment() {

    private val viewModel: CalculationFlowViewModel by navGraphViewModels(R.id.calculation_flow_graph) {
        appViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rateSpinner = view.findViewById<Spinner>(R.id.spinnerRate)
        val topUpInput = view.findViewById<TextInputEditText>(R.id.etMonthlyTopUp)
        val errorText = view.findViewById<TextView>(R.id.tvCalculationError)
        val backButton = view.findViewById<Button>(R.id.btnBackToStepOne)
        val calculateButton = view.findViewById<Button>(R.id.btnCalculate)
        val rates = viewModel.getAvailableRates()

        if (rates.isEmpty()) {
            Toast.makeText(requireContext(), "Fill step 1 first", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        rateSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            rates.map { "$it %" }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (topUpInput.text.isNullOrEmpty()) {
                        topUpInput.setText(state.monthlyTopUp)
                    }
                    errorText.isVisible = !state.error.isNullOrBlank()
                    errorText.text = state.error
                    val selectedRate = state.selectedRate
                    val selectedIndex = if (selectedRate == null) 0 else rates.indexOf(selectedRate)
                    if (selectedIndex >= 0) {
                        rateSpinner.setSelection(selectedIndex)
                    }
                }
            }
        }

        backButton.setOnClickListener {
            viewModel.clearError()
            findNavController().popBackStack()
        }

        calculateButton.setOnClickListener {
            viewModel.setStepTwoData(
                selectedRate = rates[rateSpinner.selectedItemPosition],
                monthlyTopUp = topUpInput.text?.toString().orEmpty()
            )
            if (viewModel.calculate()) {
                findNavController().navigate(R.id.action_stepTwoFragment_to_resultFragment)
            }
        }
    }
}
