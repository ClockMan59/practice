package mobile.presentation

import ci.nsu.mobile.main.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import mobile.DepositApp

class MainActivity : AppCompatActivity() {

    // Объявляем нашу ViewModel, чтобы она была доступна во всем классе
    private lateinit var viewModel: DepositViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Подключаем дизайн экрана (файл activity_main.xml)
        setContentView(R.layout.activity_main)

        // 1. ДОСТАЕМ БАЗУ И РЕПОЗИТОРИЙ
        // Обращаемся к нашему DepositApp, который мы прописали в Манифесте
        val repository = (application as DepositApp).repository

        // 2. СОЗДАЕМ ФАБРИКУ И VIEWMODEL
        val factory = DepositViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DepositViewModel::class.java]

        // 3. НАХОДИМ ЭЛЕМЕНТЫ НА ЭКРАНЕ (по их ID из XML)
        // ВАЖНО: Тебе нужно будет убедиться, что в activity_main.xml у тебя именно такие ID
        val etAmount = findViewById<EditText>(R.id.etAmount)       // Поле для суммы
        val etPercent = findViewById<EditText>(R.id.etPercent)     // Поле для процента
        val etMonths = findViewById<EditText>(R.id.etMonths)       // Поле для срока (месяцев)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate) // Кнопка "Рассчитать"
        val tvResult = findViewById<TextView>(R.id.tvResult)       // Текст для вывода результата

        // 4. ОЖИВЛЯЕМ КНОПКУ
        btnCalculate.setOnClickListener {
            // Считываем текст, который ввел юзер
            val amountStr = etAmount.text.toString()
            val percentStr = etPercent.text.toString()
            val monthsStr = etMonths.text.toString()

            // Проверяем, чтобы поля не были пустыми
            if (amountStr.isEmpty() || percentStr.isEmpty() || monthsStr.isEmpty()) {
                Toast.makeText(this, "Бро, заполни все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Переводим текст в числа
            val amount = amountStr.toDouble()
            val percent = percentStr.toDouble()
            val months = monthsStr.toInt()

            // Простейшая формула расчета (Сумма * Процент / 100 * Месяцы / 12) + Сумма
            // В идеале эта логика должна быть внутри ViewModel, но для лабы пойдет и так!
            val profit = (amount * percent / 100) * (months / 12.0)
            val total = amount + profit

            // Выводим результат на экран
            tvResult.text = "Итоговая сумма: ${String.format("%.2f", total)} ₽\nПрибыль: ${String.format("%.2f", profit)} ₽"

            // ТУТ МЫ СОХРАНЯЕМ В БАЗУ ДАННЫХ
            // Если у тебя во ViewModel есть метод для сохранения (например, insert или save),
            // ты вызываешь его здесь. Например:
            // viewModel.saveCalculation(DepositCalculation(amount = amount, percent = percent, result = total))
        }
    }
}