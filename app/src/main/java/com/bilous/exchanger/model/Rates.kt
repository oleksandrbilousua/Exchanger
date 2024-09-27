package com.bilous.exchanger.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rates(
    @SerialName("list")
    val list: List<CurrencyRate>
)

fun Rates.toMap(): Map<String, Float> =
    list.associate { it.currency to it.rate }

