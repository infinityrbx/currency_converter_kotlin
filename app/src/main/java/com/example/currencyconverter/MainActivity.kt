package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val countries = arrayOf("Euro", "Dollar", "Won", "Yen", "Dong")
    private val symbols = arrayOf("€", "$", "₩", "¥", "đ")
    private val exchangeRates = floatArrayOf(1f, 1.12f, 0.00085f, 0.0075f, 0.000043f)

    private var positionFrom = 0
    private var positionTo = 1
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextInput: EditText
    private lateinit var editTextOutput: EditText
    private lateinit var textViewUnitFrom: TextView
    private lateinit var textViewUnitTo: TextView
    private lateinit var textViewExchangeRate: TextView

    private var isUpdatingOutput = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setUpSpinners()
        setEditTextListeners()
        updateExchangeRate()
    }

    private fun initializeViews() {
        spinnerFrom = findViewById(R.id.spinner_from)
        spinnerTo = findViewById(R.id.spinner_to)
        editTextInput = findViewById(R.id.edit_text_input)
        editTextOutput = findViewById(R.id.edit_text_output)
        textViewUnitFrom = findViewById(R.id.text_view_unit_from)
        textViewUnitTo = findViewById(R.id.text_view_unit_to)
        textViewExchangeRate = findViewById(R.id.tv_exchange_rate)
    }

    private fun setUpSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerFrom.onItemSelectedListener = this

        spinnerTo.adapter = adapter
        spinnerTo.onItemSelectedListener = this
    }

    private fun setEditTextListeners() {
        editTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingOutput && editTextInput.hasFocus()) {
                    updateExchange()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextOutput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isUpdatingOutput && editTextOutput.hasFocus()) {
                    updateReverseExchange()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (parent.id == R.id.spinner_from) {
            positionFrom = position
            textViewUnitFrom.text = symbols[position]
        } else {
            positionTo = position
            textViewUnitTo.text = symbols[position]
        }
        updateExchange()
        updateExchangeRate()
    }

    private fun updateExchange() {
        val inputText = editTextInput.text.toString()
        if (inputText.isNotEmpty()) {
            try {
                val inputValue = inputText.toFloat()
                val convertedValue = (inputValue * exchangeRates[positionFrom]) / exchangeRates[positionTo]

                isUpdatingOutput = true
                editTextOutput.setText(String.format("%.2f", convertedValue))
                isUpdatingOutput = false
            } catch (e: NumberFormatException) {
                Log.e("CurrencyConverter", "Invalid input: $inputText", e)
            }
        } else {
            editTextOutput.setText("")
        }
    }

    private fun updateReverseExchange() {
        val outputText = editTextOutput.text.toString()
        if (outputText.isNotEmpty()) {
            try {
                val outputValue = outputText.toFloat()
                val convertedValue = (outputValue * exchangeRates[positionTo]) / exchangeRates[positionFrom]

                isUpdatingOutput = true
                editTextInput.setText(String.format("%.2f", convertedValue))
                isUpdatingOutput = false
            } catch (e: NumberFormatException) {
                Log.e("CurrencyConverter", "Invalid output: $outputText", e)
            }
        } else {
            editTextInput.setText("")
        }
    }

    private fun updateExchangeRate() {
        val exchangeRateValue = exchangeRates[positionFrom] / exchangeRates[positionTo]
        textViewExchangeRate.text = String.format("1 %s = %.4f %s", symbols[positionFrom], exchangeRateValue, symbols[positionTo])
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
