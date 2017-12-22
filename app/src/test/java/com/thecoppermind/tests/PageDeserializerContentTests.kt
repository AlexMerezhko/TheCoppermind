@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests

import com.thecoppermind.robots.parse
import org.junit.Test

class PageDeserializerContentTests {

    @Test
    fun `Empty string`() {
        parse("") {
            match()
        }
    }
}