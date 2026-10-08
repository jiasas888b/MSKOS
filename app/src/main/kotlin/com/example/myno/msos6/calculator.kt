package com.example.myno.msos6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class calculator : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    
    private var currentInput = ""
    private var lastOperator = ""
    private var lastNumber = ""
    private var result = 0.0
    private var isOperatorPressed = false
    
    var app_version = "1.0.5"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calmain)
        
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
        
        setupButtons()
        resetCalculator()
    }
    
    private fun setupButtons() {
        // 数字按钮
        findViewById<Button>(R.id.btn0).setOnClickListener { appendNumber("0") }
        findViewById<Button>(R.id.btn1).setOnClickListener { appendNumber("1") }
        findViewById<Button>(R.id.btn2).setOnClickListener { appendNumber("2") }
        findViewById<Button>(R.id.btn3).setOnClickListener { appendNumber("3") }
        findViewById<Button>(R.id.btn4).setOnClickListener { appendNumber("4") }
        findViewById<Button>(R.id.btn5).setOnClickListener { appendNumber("5") }
        findViewById<Button>(R.id.btn6).setOnClickListener { appendNumber("6") }
        findViewById<Button>(R.id.btn7).setOnClickListener { appendNumber("7") }
        findViewById<Button>(R.id.btn8).setOnClickListener { appendNumber("8") }
        findViewById<Button>(R.id.btn9).setOnClickListener { appendNumber("9") }
        
        // 运算符按钮
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("÷") }
        
        // 功能按钮
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }
        findViewById<Button>(R.id.btnDot).setOnClickListener { addDot() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { plusMinus() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { percent() }
    }
    
    private fun appendNumber(number: String) {
        if (isOperatorPressed) {
            currentInput = ""
            isOperatorPressed = false
        }
        if (currentInput.length < 15) {
            currentInput += number
            updateDisplay()
        }
    }
    
    private fun addDot() {
        if (isOperatorPressed) {
            currentInput = "0."
            isOperatorPressed = false
        } else if (currentInput.isEmpty()) {
            currentInput = "0."
        } else if (!currentInput.contains(".")) {
            currentInput += "."
        }
        updateDisplay()
    }
    
    private fun setOperator(operator: String) {
        if (currentInput.isEmpty() && lastNumber.isEmpty()) return
        
        if (currentInput.isNotEmpty() && lastOperator.isNotEmpty()) {
            calculate()
        }
        
        if (currentInput.isNotEmpty()) {
            lastNumber = currentInput
            lastOperator = operator
            isOperatorPressed = true
            updateExpression()
        } else if (lastNumber.isNotEmpty()) {
            lastOperator = operator
            updateExpression()
        }
    }
    
    private fun calculate() {
        if (lastNumber.isEmpty() || currentInput.isEmpty()) return
        
        val num1 = lastNumber.toDouble()
        val num2 = currentInput.toDouble()
        var calculationResult = 0.0
        
        when (lastOperator) {
            "+" -> calculationResult = num1 + num2
            "-" -> calculationResult = num1 - num2
            "×" -> calculationResult = num1 * num2
            "÷" -> {
                if (num2 == 0.0) {
                    tvResult.text = "错误"
                    resetCalculator()
                    return
                }
                calculationResult = num1 / num2
            }
        }
        
        val formattedResult = formatResult(calculationResult)
        tvResult.text = formattedResult
        tvExpression.text = "$lastNumber $lastOperator $currentInput ="
        
        // 准备下一次计算
        lastNumber = formattedResult
        currentInput = ""
        lastOperator = ""
        isOperatorPressed = true
    }
    
    private fun clear() {
        resetCalculator()
        tvResult.text = "0"
        tvExpression.text = ""
    }
    
    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) currentInput = "0"
            updateDisplay()
        }
    }
    
    private fun plusMinus() {
        if (currentInput.isNotEmpty()) {
            val num = currentInput.toDouble()
            currentInput = formatResult(-num)
            updateDisplay()
        }
    }
    
    private fun percent() {
        if (currentInput.isNotEmpty()) {
            val num = currentInput.toDouble() / 100
            currentInput = formatResult(num)
            updateDisplay()
        }
    }
    
    private fun formatResult(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            DecimalFormat("#.########").format(number)
        }
    }
    
    private fun updateDisplay() {
        tvResult.text = if (currentInput.isEmpty()) "0" else currentInput
    }
    
    private fun updateExpression() {
        tvExpression.text = "$lastNumber $lastOperator"
    }
    
    private fun resetCalculator() {
        currentInput = ""
        lastOperator = ""
        lastNumber = ""
        isOperatorPressed = false
    }
}