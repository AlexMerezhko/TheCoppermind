@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageTextBoldItalic
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.BoldItalic
import org.junit.Assert
import org.junit.Test


class BoldTextTests {

    @Test
    fun `check generated data`() {
        generator {
            val boldText = exampleForContentType(BoldItalic)
            Assert.assertTrue("'''$boldText'''" == exampleForContentTypeWithBorders(BoldItalic))
            Assert.assertTrue(PageTextBoldItalic(boldText) == exampleForParsedContentType(BoldItalic))

            content {
                text(exampleForContentTypeWithBorders(BoldItalic))
                match(exampleForParsedContentType(BoldItalic))
            }
        }
    }

    @Test
    fun `no bold`() {
        generator {
            content {
                text(generateDataForAllTypesExceptOne(PageTextBoldItalic::class))
                notContains<PageTextBoldItalic>()
            }
        }
    }

    @Test
    fun `empty bold`() {
        generator {
            content {
                text(wrapWithContentTypeBorders(BoldItalic, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one bold text`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(BoldItalic))
                match(PageTextBoldItalic(exampleForContentType(BoldItalic)))
            }
        }
    }

    @Test
    fun `two bold text in a row`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(BoldItalic) + exampleForContentTypeWithBorders(BoldItalic))
                match(exampleForParsedContentType(BoldItalic), exampleForParsedContentType(BoldItalic))
            }
        }
    }
}
