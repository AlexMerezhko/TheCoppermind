@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing
/*
import com.thecoppermind.page.BoldItalicType
import com.thecoppermind.robots.verifyContent
import com.thecoppermind.page.PageTextBoldItalic
import com.thecoppermind.robots.generator
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.BoldItalic
import org.junit.Assert
import org.junit.Test


class BoldTextTests {

    @Test
    fun `check generated data`() {
        generator {
            val boldText = exampleForContentType(BoldItalic)
            Assert.assertTrue("'''$boldText'''" == exampleForContentTypeWithBorders(BoldItalic))
            Assert.assertTrue(PageTextBoldItalic(boldText, BoldItalicType.bold) == exampleForParsedContentType(BoldItalic)) // TODO hardcode

            verifyContent {
                init(exampleForContentTypeWithBorders(BoldItalic))
                match(exampleForParsedContentType(BoldItalic))
            }
        }
    }

    @Test
    fun `no bold`() {
        generator {
            verifyContent {
                init(generateDataForAllTypesExceptOne(PageTextBoldItalic::class))
                notContains<PageTextBoldItalic>()
            }
        }
    }

    @Test
    fun `empty bold`() {
        generator {
            verifyContent {
                init(wrapWithContentTypeBorders(BoldItalic, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one bold text`() {
        generator {
            verifyContent {
                init(exampleForContentTypeWithBorders(BoldItalic))
//                match(PageTextBoldItalic(exampleForContentType(BoldItalic)))
            }
        }
    }

    @Test
    fun `two bold text in a row`() {
        generator {
            verifyContent {
                init(exampleForContentTypeWithBorders(BoldItalic) + exampleForContentTypeWithBorders(BoldItalic))
                match(exampleForParsedContentType(BoldItalic), exampleForParsedContentType(BoldItalic))
            }
        }
    }
}
*/