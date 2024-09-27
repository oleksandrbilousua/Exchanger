package com.bilous.exchanger.util

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import com.bilous.exchanger.util.Constants.Companion.USER_ATTEMPTS
import com.bilous.exchanger.util.Constants.Companion.USER_PREF
import javax.inject.Inject

class UserPreferences @Inject constructor(context: Application) {

    private val prefs = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE)

    var exchangeAttempts: Int
        get() = prefs.getInt(USER_ATTEMPTS, 0)
        set(value) {
            prefs.edit { putInt(USER_ATTEMPTS, value) }
        }
}