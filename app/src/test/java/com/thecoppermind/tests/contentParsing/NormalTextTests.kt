@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageTextNormal
import com.thecoppermind.Robots.generator
import org.junit.Test

class NormalTextTests {

    @Test
    fun `no normal text`() {
        generator {
            content {
                text(generateDataForAllTypesExceptOne(PageTextNormal::class))
                notContains<PageTextNormal>()
            }
        }
    }
}