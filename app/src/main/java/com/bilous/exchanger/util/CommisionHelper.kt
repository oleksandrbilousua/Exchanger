package com.bilous.exchanger.util

import com.bilous.exchanger.util.Constants.Companion.DEFAULT_FEE
import com.bilous.exchanger.util.Constants.Companion.FREE_FEE_LIMIT

/**
 * Add your commission rules here
 */
fun calculateCommission(userAttempts: Int): Float {
    return if (userAttempts < FREE_FEE_LIMIT) 0f else DEFAULT_FEE
}