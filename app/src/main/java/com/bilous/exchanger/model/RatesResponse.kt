package com.bilous.exchanger.model

import com.bilous.exchanger.util.RatesSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatesResponse(
    @SerialName("base")
    val base: String,

    @SerialName("date")
    val date: String,

    @SerialName("rates")
    @Serializable(with = RatesSerializer::class)
    val currencyRates: Rates
)
