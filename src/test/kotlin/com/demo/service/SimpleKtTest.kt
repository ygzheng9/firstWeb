package com.demo.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimpleKtTest {

    @Test
    fun getPoint2Test() {
        val s = getPoint2(95)

        assertEquals("GOOD", s)
    }
}
