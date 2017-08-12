@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.HeadingLevel
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Heading
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Link
import com.thecoppermind.page.PageTextHeading
import com.thecoppermind.page.PageTextLink
import com.thecoppermind.page.PageTextNormal
import com.thecoppermind.utils.getIdForLink
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertTrue

class HeadingTests {

    @Test
    fun `check generated data`() {
        generator {
            val headingText = exampleForContentType(Heading)
            assertTrue("==$headingText==" == exampleForContentTypeWithBorders(Heading))
            assertTrue(PageTextHeading(headingText) == exampleForParsedContentType(Heading))

            content {
                text(exampleForContentTypeWithBorders(Heading))
                match(exampleForParsedContentType(Heading))
            }
        }
    }

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
                text(wrapWithContentTypeBorders(Heading, " "))
                matchResultEmpty()
            }
            content {
                text(wrapWithContentTypeBorders(Heading, "  "))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one heading`() {
        val baseText = "This is Heading"
        for (bordersCount in 2..6) {
            var borders = ""
            for (borderPosition in 0 until bordersCount) {
                borders += "="
            }
            content {
                text("$borders$baseText$borders")
                match(PageTextHeading(text = baseText, level = HeadingLevel.getLevelTypeFromBorderCharsCount(bordersCount)))
            }
        }
    }

    @Test
    fun `get heading level from borders count`() {
        for (bordersCount in HeadingLevel.values()[0].countOfBorderChars..HeadingLevel.values()[HeadingLevel.values().size - 1].countOfBorderChars) {
            assertEquals(
                    HeadingLevel.getLevelTypeFromBorderCharsCount(bordersCount),
                    when (bordersCount) {
                        2 -> HeadingLevel.h2
                        3 -> HeadingLevel.h3
                        4 -> HeadingLevel.h4
                        5 -> HeadingLevel.h5
                        6 -> HeadingLevel.h6
                        else -> TODO("Test failed")
                    }
            )
        }
    }

    @Test
    fun `two heading in a row`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(Heading) + exampleForContentTypeWithBorders(Heading))
                match(exampleForParsedContentType(Heading), exampleForParsedContentType(Heading))
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

    @Test
    fun `removing new lines after heading`() {
        generator {
            val baseText = exampleForContentTypeWithBorders(Heading) + '\n'
            val expectedResult = exampleForParsedContentType(Heading)
            content {
                text(baseText)
                match(expectedResult)
            }
        }

        generator {
            val baseText = exampleForContentTypeWithBorders(Heading) + '\n' + '\n' + '\n'
            val expectedResult = exampleForParsedContentType(Heading)
            content {
                text(baseText)
                match(expectedResult)
            }
        }

        generator {
            val baseText = exampleForContentTypeWithBorders(Heading) + '\n' + exampleNormalText()
            val expectedResultHeading = exampleForParsedContentType(Heading)
            val expectedResultNormalText = exampleParsedNormalText()
            content {
                text(baseText)
                match(expectedResultHeading, expectedResultNormalText)
            }
        }
    }

    @Test
    fun `heading with link`() {
        content {
            val items = listOf(PageTextNormal("This is heading"), PageTextLink("with link", "with link".getIdForLink()))
            text("==This is heading[[with link]]==")
            match(PageTextHeading("This is heading[[with link]]", items))
        }
    }

    @Test
    fun `heading with link and spaces`() {
        content {
            val items = listOf(PageTextNormal("This is heading"), PageTextLink("with link", "with link".getIdForLink()))
            text("== This is heading[[with link]] ==")
            match(PageTextHeading("This is heading[[with link]]", items))
        }
        generator {
            content {
                val items = listOf(exampleParsedNormalText(), exampleForParsedContentType(Link))
                val headingText = exampleNormalText() + exampleForContentTypeWithBorders(Link)
                text(wrapWithContentTypeBorders(Heading, " $headingText "))
                match(PageTextHeading(headingText, items))
            }
        }
    }

    @Test
    fun `heading with link and postfix`() {

        val firstText = "This is heading "
        val linkText = "with link"
        val postfix = "and"
        val afterPostfix = " postfix"

        generator {
            content {
                val items = listOf(PageTextNormal(firstText), PageTextLink(linkText + postfix, linkText.getIdForLink()), PageTextNormal(afterPostfix))
                val headingText = firstText + wrapWithContentTypeBorders(Link, linkText) + postfix + afterPostfix
                text(wrapWithContentTypeBorders(Heading, headingText))
                match(PageTextHeading(headingText, items))
            }
        }
    }


    @Test
    fun `heading with two links`() {
        generator {
            content {
                val items = listOf(exampleParsedNormalText(), exampleForParsedContentType(Link), exampleForParsedContentType(Link))
                val headingText = exampleNormalText() + exampleForContentTypeWithBorders(Link) + exampleForContentTypeWithBorders(Link)
                text(wrapWithContentTypeBorders(Heading, headingText))
                match(PageTextHeading(headingText, items))
            }
            content {
                val items = listOf(exampleForParsedContentType(Link), exampleForParsedContentType(Link))
                val headingText = exampleForContentTypeWithBorders(Link) + exampleForContentTypeWithBorders(Link)
                text(wrapWithContentTypeBorders(Heading, headingText))
                match(PageTextHeading(headingText, items))
            }
        }
        content {
            val items = listOf(PageTextNormal("This is heading"), PageTextLink("with link", "with link".getIdForLink()), PageTextNormal(" This is heading"))
            text("==This is heading[[with link]] This is heading==")
            match(PageTextHeading("This is heading[[with link]] This is heading", items))
        }

        content {
            val items = listOf(PageTextLink("with link", "with link".getIdForLink()), PageTextNormal(" This is heading"), PageTextLink("with link", "with link".getIdForLink()))
            text("==[[with link]] This is heading[[with link]]==")
            match(PageTextHeading("[[with link]] This is heading[[with link]]", items))
        }
    }
}
