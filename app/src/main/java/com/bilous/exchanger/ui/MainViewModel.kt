package com.bilous.exchanger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilous.exchanger.model.UserCurrency
import com.bilous.exchanger.model.toMap
import com.bilous.exchanger.repository.ExchangeRepository
import com.bilous.exchanger.util.Constants.Companion.DEFAULT_BALANCE
import com.bilous.exchanger.util.Constants.Companion.DEFAULT_CURRENCY
import com.bilous.exchanger.util.Constants.Companion.RATES_REFRESH_DELAY
import com.bilous.exchanger.util.UserPreferences
import com.bilous.exchanger.util.calculateCommission
import com.bilous.exchanger.util.validateInputs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExchangeRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _rates = MutableLiveData<Map<String, Float>>(emptyMap())
    val rates: LiveData<Map<String, Float>> = _rates

    private val _resultValue = MutableLiveData(0f)
    val resultValue: LiveData<Float> = _resultValue

    private val _status = MutableLiveData<Result>()
    val status: LiveData<Result> = _status

    private var sellCurrency = ""
    private var sellValue = 0f
    private var resultCurrency = ""


    sealed class Result {
        data class Success(
            val sellValue: Float, val sellCurrency: String,
            val resultCurrency: String, val resultValue: Float
        ) : Result()

        data class SuccessWithFee(
            val sellValue: Float, val sellCurrency: String,
            val resultCurrency: String, val resultValue: Float, val fee: Float
        ) : Result()

        data object Failure : Result()
    }

    init {
        fetch()
    }

    suspend fun readUserBalance() = repository.readUserBalance()

    fun exchange() {
        viewModelScope.launch {
            val balance = repository.readUserBalance().first()
            if (validateInputs(sellCurrency, resultCurrency, sellValue, balance)) {
                updateUserBalance()
                val commission = commission()
                userPreferences.exchangeAttempts++
                if (commission != 0f) {
                    _status.value = Result.SuccessWithFee(
                        sellValue, sellCurrency,
                        resultCurrency, _resultValue.value!!, commission * 100
                    )
                } else {
                    _status.value = Result.Success(
                        sellValue, sellCurrency,
                        resultCurrency, _resultValue.value!!
                    )
                }
            } else {
                _status.value = Result.Failure
            }
        }
    }

    fun calculateResult(sellCurrency: String, sellValue: Float, resultCurrency: String) {
        this.sellCurrency = sellCurrency
        this.sellValue = sellValue
        this.resultCurrency = resultCurrency
        rates.value?.let {
            val sellRate = it[sellCurrency]
            val resultRate = it[resultCurrency]
            if (sellRate != null && resultRate != null)
                _resultValue.value = sellValue * resultRate / sellRate * (1 - commission())
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            val balance = repository.readUserBalance().first()
            //set default balance for the first time
            if (balance.isEmpty()) {
                repository.updateUserBalance(UserCurrency(DEFAULT_CURRENCY, DEFAULT_BALANCE))
            }
            while (isActive) {
                _rates.value = repository.fetchRates().currencyRates.toMap()
                delay(RATES_REFRESH_DELAY)
            }
        }
    }

    private fun updateUserBalance() {
        viewModelScope.launch {
            val balance = repository.readUserBalance().first()
            val sellBalance = balance.findLast { it.currency == sellCurrency }
            sellBalance?.let {
                repository.updateUserBalance(it.copy(balance = it.balance - sellValue))
            }
            val resultBalance = balance.findLast { it.currency == resultCurrency }
            resultBalance?.let {
                repository.updateUserBalance(it.copy(balance = it.balance + resultValue.value!!))
            } ?: run {
                repository.updateUserBalance(UserCurrency(resultCurrency, resultValue.value!!))

            }
        }
    }

    private fun commission(): Float = calculateCommission(userPreferences.exchangeAttempts)

}