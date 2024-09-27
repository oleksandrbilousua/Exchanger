package com.bilous.exchanger

import com.bilous.exchanger.util.Constants.Companion.DEFAULT_FEE
import com.bilous.exchanger.util.calculateCommission
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculationTest {

    @Test

    fun `user should not have any fee for first 5 attempts`(){
        assertThat(calculateCommission(3)).isEqualTo(0f)
        assertThat(calculateCommission(7)).isEqualTo(DEFAULT_FEE)
    }

}