package ci.nsu.mobile.main.ui.calculations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.data.db.DepositCalculation
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyCalculationsFragment : Fragment() {

    private val viewModel: MyCalculationsViewModel by viewModels { appViewModelFactory() }
    private val adapter = CalculationAdapter(::showDetails, ::confirmDelete)
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_calculations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateInput = view.findViewById<TextInputEditText>(R.id.etFilterDate)
        val minAmountInput = view.findViewById<TextInputEditText>(R.id.etFilterMinAmount)
        val maxAmountInput = view.findViewById<TextInputEditText>(R.id.etFilterMaxAmount)
        val rateInput = view.findViewById<TextInputEditText>(R.id.etFilterRate)
        val applyButton = view.findViewById<Button>(R.id.btnApplyFilters)
        val clearButton = view.findViewById<Button>(R.id.btnClearFilters)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCalculations)
        val emptyText = view.findViewById<TextView>(R.id.tvCalculationsEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        applyButton.setOnClickListener {
            viewModel.updateFilters(
                dateQuery = dateInput.text?.toString().orEmpty(),
                minAmount = minAmountInput.text?.toString().orEmpty(),
                maxAmount = maxAmountInput.text?.toString().orEmpty(),
                rateQuery = rateInput.text?.toString().orEmpty()
            )
        }

        clearButton.setOnClickListener {
            dateInput.setText("")
            minAmountInput.setText("")
            maxAmountInput.setText("")
            rateInput.setText("")
            viewModel.updateFilters("", "", "", "")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activeFilter.collect { filter ->
                    if (dateInput.text.isNullOrEmpty()) {
                        dateInput.setText(filter.dateQuery)
                    }
                    if (minAmountInput.text.isNullOrEmpty()) {
                        minAmountInput.setText(filter.minAmount)
                    }
                    if (maxAmountInput.text.isNullOrEmpty()) {
                        maxAmountInput.setText(filter.maxAmount)
                    }
                    if (rateInput.text.isNullOrEmpty()) {
                        rateInput.setText(filter.rateQuery)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.calculations.collect { items ->
                    adapter.submitList(items)
                    emptyText.isVisible = items.isEmpty()
                }
            }
        }
    }

    private fun showDetails(calculation: DepositCalculation) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Детали расчета")
            .setMessage(
                "Дата: ${formatter.format(Date(calculation.calculationDate))}\n" +
                    "Стартовая сумма: ${"%.2f".format(calculation.initialAmount)} ₽\n" +
                    "Срок: ${calculation.periodMonths} мес.\n" +
                    "Ставка: ${calculation.interestRate}%\n" +
                    "Пополнение: ${calculation.monthlyTopUp ?: 0.0} ₽\n" +
                    "Доход: ${"%.2f".format(calculation.interestEarned)} ₽\n" +
                    "Итог: ${"%.2f".format(calculation.finalAmount)} ₽"
            )
            .setPositiveButton("Закрыть", null)
            .show()
    }

    private fun confirmDelete(calculation: DepositCalculation) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить расчет?")
            .setMessage("Запись будет удалена из локальной истории текущего пользователя.")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteCalculation(calculation)
            }
            .show()
    }
}
