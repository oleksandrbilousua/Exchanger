package com.bilous.exchanger.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class UserCurrency(
    @PrimaryKey
    val currency: String,
    val balance: Float
) : Parcelable


