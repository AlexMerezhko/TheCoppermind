@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageTextBold
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Bold
import org.junit.Assert
import org.junit.Test


class BoldTextTests {

    @Test
    fun `check generated data`() {
        generator {
            val boldText = exampleForContentType(Bold)
            Assert.assertTrue("'''$boldText'''" == exampleForContentTypeWithBorders(Bold))
            Assert.assertTrue(PageTextBold(boldText) == exampleForParsedContentType(Bold))

            content {
                text(exampleForContentTypeWithBorders(Bold))
                match(exampleForParsedContentType(Bold))
            }
        }
    }

    @Test
    fun `no bold`() {
        generator {
            content {
                text(generateDataForAllTypesExceptOne(PageTextBold::class))
                notContains<PageTextBold>()
            }
        }
    }

    @Test
    fun `empty bold`() {
        generator {
            content {
                text(wrapWithContentTypeBorders(Bold, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one bold text`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(Bold))
                match(PageTextBold(exampleForContentType(Bold)))
            }
        }
    }

    @Test
    fun `two bold text in a row`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(Bold) + exampleForContentTypeWithBorders(Bold))
                match(exampleForParsedContentType(Bold), exampleForParsedContentType(Bold))
            }
        }
    }
}
