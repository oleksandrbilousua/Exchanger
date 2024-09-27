package com.bilous.exchanger.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bilous.exchanger.model.UserCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCurrency(userCurrency: UserCurrency)

    @Query("SELECT * FROM UserCurrency")
    fun getBalances(): Flow<List<UserCurrency>>
}
