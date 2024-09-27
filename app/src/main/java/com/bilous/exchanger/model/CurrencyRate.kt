package com.bilous.exchanger.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyRate(
    @SerialName("currency")
    val currency: String,

    @SerialName("rate")
    val rate: Float
)
