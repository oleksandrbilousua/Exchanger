package com.bilous.exchanger.util

import com.bilous.exchanger.model.UserCurrency

fun validateInputs(
    sellCurrency: String, resultCurrency: String,
    sellValue: Float, balance: List<UserCurrency>
): Boolean {

    if (sellCurrency == resultCurrency)
        return false

    val sellBalance = balance.findLast { it.currency == sellCurrency }

    return sellBalance?.let {
        sellBalance.balance >= sellValue

    } ?: false

}


