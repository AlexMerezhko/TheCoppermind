@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests

import com.thecoppermind.Robots.content
import org.junit.Test

class PageDeserializerContentTests {

    @Test
    fun `Empty string`() {
        content {
            text("")
            match()
        }
    }
}