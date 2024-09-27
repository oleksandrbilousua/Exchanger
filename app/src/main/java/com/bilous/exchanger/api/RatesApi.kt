package com.bilous.exchanger.api

import com.bilous.exchanger.model.RatesResponse
import retrofit2.http.GET

interface RatesApi {

    @GET("tasks/api/currency-exchange-rates")
    suspend fun fetchRates(): RatesResponse
}