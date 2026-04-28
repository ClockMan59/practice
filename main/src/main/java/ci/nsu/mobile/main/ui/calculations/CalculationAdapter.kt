package ci.nsu.mobile.main.ui.calculations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.data.db.DepositCalculation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalculationAdapter(
    private val onClick: (DepositCalculation) -> Unit,
    private val onDelete: (DepositCalculation) -> Unit
) : ListAdapter<DepositCalculation, CalculationAdapter.CalculationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calculation, parent, false)
        return CalculationViewHolder(view, onClick, onDelete)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalculationViewHolder(
        itemView: View,
        private val onClick: (DepositCalculation) -> Unit,
        private val onDelete: (DepositCalculation) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val dateText: TextView = itemView.findViewById(R.id.tvCalcDate)
        private val summaryText: TextView = itemView.findViewById(R.id.tvCalcSummary)
        private val resultText: TextView = itemView.findViewById(R.id.tvCalcResult)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteCalculation)

        fun bind(item: DepositCalculation) {
            dateText.text = dateFormat.format(Date(item.calculationDate))
            summaryText.text =
                "Сумма ${"%.2f".format(item.initialAmount)} ₽, срок ${item.periodMonths} мес., ставка ${item.interestRate}%"
            resultText.text = "Итог ${"%.2f".format(item.finalAmount)} ₽"
            itemView.setOnClickListener { onClick(item) }
            deleteButton.setOnClickListener { onDelete(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<DepositCalculation>() {
        override fun areItemsTheSame(
            oldItem: DepositCalculation,
            newItem: DepositCalculation
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DepositCalculation,
            newItem: DepositCalculation
        ): Boolean {
            return oldItem == newItem
        }
    }
}
