package com.bilous.exchanger.di

import android.app.Application
import androidx.room.Room
import com.bilous.exchanger.api.RatesApi
import com.bilous.exchanger.db.BalanceDao
import com.bilous.exchanger.db.BalanceDatabase
import com.bilous.exchanger.repository.ExchangeRepository
import com.bilous.exchanger.util.Constants
import com.bilous.exchanger.util.UserPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRatesApi(json:Json): RatesApi {
        val contentType = "application/json".toMediaType()
        val converterFactory = json.asConverterFactory(contentType)
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
            .create(RatesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNoteDatabase(context: Application): BalanceDatabase {
        return Room.databaseBuilder(
            context,
            BalanceDatabase::class.java,
            "balance_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(balanceDatabase: BalanceDatabase): BalanceDao = balanceDatabase.balanceDao


    @Provides
    @Singleton
    fun provideRepository(ratesApi: RatesApi, balanceDao: BalanceDao) = ExchangeRepository(ratesApi,balanceDao)

    @Provides
    @Singleton
    fun repository(context: Application) = UserPreferences(context)

}