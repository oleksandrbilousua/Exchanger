package com.bilous.exchanger.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bilous.exchanger.model.UserCurrency


@Database(entities = [UserCurrency::class], version = 1, exportSchema = false)
abstract class BalanceDatabase : RoomDatabase() {
    abstract val balanceDao: BalanceDao
}