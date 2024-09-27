package com.bilous.exchanger

import com.bilous.exchanger.model.UserCurrency
import com.bilous.exchanger.util.validateInputs
import com.google.common.truth.Truth.assertThat
import org.junit.Test


class ValidatorTest {

    @Test
    fun `sell and receive currency should be different`() {
        val sellCurrency = "EUR"
        val receiveCurrency = "EUR"
        val sellValue = 80f
        val userBalance = listOf(UserCurrency("EUR", 100f))
        assertThat(validateInputs(sellCurrency, receiveCurrency, sellValue, userBalance)).isFalse()
    }

    @Test
    fun `sell currency should be in user balance`() {
        val sellCurrency = "UAH"
        val receiveCurrency = "USD"
        val sellValue = 80f
        val userBalance = listOf(UserCurrency("EUR", 100f))
        assertThat(validateInputs(sellCurrency, receiveCurrency, sellValue, userBalance)).isFalse()
    }

    @Test
    fun `balance should have enough currency amount to sell`() {
        val sellCurrency = "EUR"
        val receiveCurrency = "USD"
        val sellValue = 120f
        val userBalance = listOf(UserCurrency("EUR", 100f))
        assertThat(validateInputs(sellCurrency, receiveCurrency, sellValue, userBalance)).isFalse()
    }
}