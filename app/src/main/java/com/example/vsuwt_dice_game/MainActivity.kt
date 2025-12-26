package com.example.vsuwt_dice_game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val rollHistory = mutableListOf<String>()
    private var totalRolls = 0
    private var maxHistoryItems = 10 // Ограничиваем историю 10 записями

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRoll = findViewById<Button>(R.id.buttonRoll)
        val editTextDiceCount = findViewById<EditText>(R.id.editTextDiceCount)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)
        val textViewSum = findViewById<TextView>(R.id.textViewSum)
        val textViewHistory = findViewById<TextView>(R.id.textViewHistory)
        val diceContainer = findViewById<LinearLayout>(R.id.diceContainer)

        buttonRoll.setOnClickListener {
            try {
                // Получаем количество кубиков
                val diceCount = try {
                    editTextDiceCount.text.toString().toInt().coerceIn(1, 6) // Ограничиваем 6 кубиками
                } catch (e: Exception) {
                    2
                }

                // Обновляем поле ввода
                editTextDiceCount.setText(diceCount.toString())

                // Бросаем кубики
                val results = rollDice(diceCount)

                // Обновляем интерфейс
                updateUI(results, diceContainer, textViewResult, textViewSum)

                // Обновляем историю
                updateHistory(results, textViewHistory)
            } catch (e: Exception) {
                // Обработка ошибок чтобы приложение не вылетало
                textViewResult.text = "Ошибка: ${e.message}"
            }
        }
    }

    private fun rollDice(count: Int): List<Int> {
        return List(count) { Random.nextInt(1, 7) }
    }

    private fun updateUI(
        results: List<Int>,
        diceContainer: LinearLayout,
        textViewResult: TextView,
        textViewSum: TextView
    ) {
        // Очищаем контейнер ПРАВИЛЬНЫМ способом
        diceContainer.removeAllViews()

        // Создаем view для каждого кубика с оптимизацией
        for (result in results) {
            val diceView = TextView(this).apply {
                text = "🎲$result"
                textSize = 24f
                setPadding(16, 8, 16, 8)
                setBackgroundResource(android.R.drawable.btn_default)
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 8, 8, 8)
            diceView.layoutParams = layoutParams

            diceContainer.addView(diceView)
        }

        // Обновляем текст результата
        textViewResult.text = if (results.size == 1) {
            "Выпало: ${results[0]}"
        } else {
            "Результаты: ${results.joinToString(", ")}"
        }

        // Обновляем сумму
        val sum = results.sum()
        textViewSum.text = "Сумма: $sum"
    }

    private fun updateHistory(results: List<Int>, textViewHistory: TextView) {
        totalRolls++

        // Создаем текст результата
        val resultText = if (results.size == 1) {
            "Бросок $totalRolls: ${results[0]}"
        } else {
            "Бросок $totalRolls: ${results.joinToString("+")} = ${results.sum()}"
        }

        // Добавляем в начало и ограничиваем размер
        rollHistory.add(0, resultText)
        if (rollHistory.size > maxHistoryItems) {
            rollHistory.removeAt(rollHistory.size - 1) // Удаляем последний элемент
        }
// Обновляем отображение истории
        val historyText = if (rollHistory.isNotEmpty()) {
            "История (последние $maxHistoryItems бросков):\n${rollHistory.joinToString("\n")}"
        } else {
            "История бросков будет отображаться здесь"
        }
        textViewHistory.text = historyText
    }

    // Добавляем очистку ресурсов при уничтожении активности
    override fun onDestroy() {
        super.onDestroy()
        // Очищаем историю чтобы освободить память
        rollHistory.clear()
    }
}