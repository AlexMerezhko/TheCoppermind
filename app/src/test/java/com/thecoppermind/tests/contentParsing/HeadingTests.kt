@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextHeading
import org.junit.Test


class HeadingTests {

    @Test
    fun `no heading`() {
        generator {
            content {
                text(generateDataForAllTypesExceptOne(PageTextHeading::class))
                notContains<PageTextHeading>()
            }
        }
    }

    @Test
    fun `empty heading`() {
        generator {
            content {
                text(wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Heading, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one heading`() {
        generator {
            content {
                text(wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Heading))
                match(PageTextHeading(generateDataForContentType(PageClassDeserializer.Companion.ContentType.Heading)))
            }
        }
    }

    @Test
    fun `removing white spaces from heading`() {
        val baseText = "This is heading with white spaces"
        val expectedResult = PageTextHeading(baseText)

        var allTexts = ""
        for (i in 0..2) {
            var spaces: String = " "
            for (j in 0..i) spaces += " "

            var text = "==$spaces$baseText=="
            content {
                text(text)
                match(expectedResult)
            }
            allTexts += text

            text = "==$baseText$spaces=="
            content {
                text(text)
                match(expectedResult)
            }
            allTexts += text

            text = "==$spaces$baseText$spaces=="
            content {
                text(text)
                match(expectedResult)
            }
            allTexts += text
        }
        content {
            text(allTexts)
            match(expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult)
        }
    }
}
