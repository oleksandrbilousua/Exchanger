package com.bilous.exchanger.repository

import com.bilous.exchanger.api.RatesApi
import com.bilous.exchanger.db.BalanceDao
import com.bilous.exchanger.model.UserCurrency
import javax.inject.Inject

class ExchangeRepository @Inject constructor(
    private val ratesApi: RatesApi,
    private val balanceDao: BalanceDao
) {

    suspend fun fetchRates() = ratesApi.fetchRates()

    suspend fun readUserBalance() = balanceDao.getBalances()

    suspend fun updateUserBalance(userCurrency: UserCurrency) = balanceDao.upsertCurrency(userCurrency)
}