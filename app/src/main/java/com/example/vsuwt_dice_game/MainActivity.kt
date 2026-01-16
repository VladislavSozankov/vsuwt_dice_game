package com.example.vsuwt_dice_game

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val rollHistory = mutableListOf<String>()
    private var totalRolls = 0
    private var maxHistoryItems = 10

    // Объявляем переменные как lateinit
    private lateinit var buttonRoll: Button
    private lateinit var editTextDiceCount: EditText
    private lateinit var textViewResult: TextView
    private lateinit var textViewSum: TextView
    private lateinit var textViewHistory: TextView
    private lateinit var diceContainer: LinearLayout
    private lateinit var buttonAuthors: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализируем все view элементы
        initViews()

        // Обработка нажатия на кнопку "Авторы"
        buttonAuthors.setOnClickListener {
            // Открываем активность с авторами
            val intent = Intent(this, AuthorsActivity::class.java)
            startActivity(intent)
        }

        // Обработка нажатия на кнопку "Бросить кубики"
        buttonRoll.setOnClickListener {
            try {
                // Получаем количество кубиков
                val diceCount = try {
                    editTextDiceCount.text.toString().toInt().coerceIn(1, 6)
                } catch (e: Exception) {
                    2
                }

                // Обновляем поле ввода
                editTextDiceCount.setText(diceCount.toString())

                // Бросаем кубики
                val results = rollDice(diceCount)

                // Обновляем интерфейс
                updateUI(results)

                // Обновляем историю
                updateHistory(results)
            } catch (e: Exception) {
                // Обработка ошибок чтобы приложение не вылетало
                textViewResult.text = "Ошибка: ${e.message}"
            }
        }
    }

    private fun initViews() {
        buttonRoll = findViewById(R.id.buttonRoll)
        editTextDiceCount = findViewById(R.id.editTextDiceCount)
        textViewResult = findViewById(R.id.textViewResult)
        textViewSum = findViewById(R.id.textViewSum)
        textViewHistory = findViewById(R.id.textViewHistory)
        diceContainer = findViewById(R.id.diceContainer)
        buttonAuthors = findViewById(R.id.buttonAuthors)
    }

    private fun rollDice(count: Int): List<Int> {
        return List(count) { Random.nextInt(1, 7) }
    }

    private fun updateUI(results: List<Int>) {
        // Очищаем контейнер
        diceContainer.removeAllViews()

        // Создаем view для каждого кубика
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

    private fun updateHistory(results: List<Int>) {
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
            rollHistory.removeAt(rollHistory.size - 1)
        }

        // Обновляем отображение истории
        val historyText = if (rollHistory.isNotEmpty()) {
            "История (последние $maxHistoryItems бросков):\n${rollHistory.joinToString("\n")}"
        } else {
            "История бросков будет отображаться здесь"
        }
        textViewHistory.text = historyText
    }

    override fun onDestroy() {
        super.onDestroy()
        rollHistory.clear()
    }
}