@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.page.HeadingLevel
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Heading
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Link
import com.thecoppermind.page.PageTextHeading
import com.thecoppermind.page.PageTextLink
import com.thecoppermind.page.PageTextPlain
import com.thecoppermind.robots.generate
import com.thecoppermind.robots.parse
import com.thecoppermind.utils.getIdForLink
import org.junit.Assert.assertEquals
import org.junit.Test

class HeadingTests {

    @Test
    fun `only heading`() {

        val headingText = "This is Heading"

        parse("==$headingText==") {
            match(PageTextHeading(headingText))
        }

        for (bordersCount in 2..6) {
            var borders = ""
            for (borderPosition in 0 until bordersCount) {
                borders += "="
            }
            parse("$borders$headingText$borders") {
                match(PageTextHeading(headingText, level = HeadingLevel.getLevelFromBorderCharsCount(bordersCount)))
            }
        }
    }

    @Test
    fun `empty heading`() {
        generate {
            parse(wrappedText(Heading, " ")) {
                matchResultEmpty()
            }
            parse(wrappedText(Heading, "  ")) {
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `get heading level from borders count`() {
        for (bordersCount in HeadingLevel.values()[0].countOfBorderChars..HeadingLevel.values()[HeadingLevel.values().size - 1].countOfBorderChars) {
            assertEquals(
                    HeadingLevel.getLevelFromBorderCharsCount(bordersCount),
                    when (bordersCount) {
                        2 -> HeadingLevel.H2
                        3 -> HeadingLevel.H3
                        4 -> HeadingLevel.H4
                        5 -> HeadingLevel.H5
                        6 -> HeadingLevel.H6
                        else -> TODO("Test failed")
                    }
            )
        }
    }

    @Test
    fun `two heading in a row`() {
        generate {
            parse(wrappedText(Heading) + wrappedText(Heading)) {
                match(parsedText(Heading), parsedText(Heading))
            }
        }
    }

    @Test
    fun `removing white spaces from heading`() {

        generate {
            val baseText = textForType(Heading)
            val expectedResult = parsedText(Heading)

            var allTexts = ""
            for (i in 0..2) {
                var spaces = " "
                for (j in 0..i) spaces += " "

                var text = "==$spaces$baseText=="
                parse(text) {
                    match(expectedResult)
                }
                allTexts += text

                text = "==$baseText$spaces=="
                parse(text) {
                    match(expectedResult)
                }
                allTexts += text

                text = "==$spaces$baseText$spaces=="
                parse(text) {
                    match(expectedResult)
                }
                allTexts += text
            }
            parse(allTexts) {
                match(expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult, expectedResult)
            }
        }
    }

    @Test
    fun `removing new lines after heading`() {
        generate {
            parse(wrappedText(Heading) + '\n') {
                match(parsedText(Heading))
            }

            parse(wrappedText(Heading) + '\n' + '\n' + '\n') {
                match(parsedText(Heading))
            }

            parse(wrappedText(Heading) + '\n' + wrappedPlainText()) {
                match(parsedText(Heading), parsedPlaintText())
            }
        }
    }

    @Test
    fun `heading with link (and spaces)`() {
        generate {
            val items = listOf(parsedPlaintText(), parsedText(Link))
            val headingText = wrappedPlainText() + wrappedText(Link)
            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
            val space = " "
            parse(wrappedText(Heading, "$space$headingText$space")) {
                match(PageTextHeading(headingText, items))
            }
        }
    }

    @Test
    fun `heading with link and postfix`() {
        val beforeLinkText = "This is heading "
        val linkText = "with link"
        val postfix = "and"
        val afterPostfix = " postfix"

        generate {
            val headingText = beforeLinkText + wrappedText(Link, linkText) + postfix + afterPostfix
            val items = listOf(PageTextPlain(beforeLinkText), PageTextLink(linkText + postfix, linkText.getIdForLink()), PageTextPlain(afterPostfix))

            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }

        // same, but less readable
        generate {
            val headingText = plainText() + wrappedText(Link) + postfix + afterPostfix
            val items = listOf(parsedPlaintText(), PageTextLink(textForType(Link) + postfix, textForType(Link).getIdForLink()), PageTextPlain(afterPostfix))

            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }
    }

    @Test
    fun `heading with two links`() {

        val space = " "

        generate {
            val items = listOf(parsedPlaintText(), parsedText(Link), parsedText(Link))
            val headingText = wrappedPlainText() + wrappedText(Link) + wrappedText(Link)
            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }

        generate {
            val items = listOf(parsedPlaintText(), parsedText(Link), parsedPlaintText(space + plainText()))
            val headingText = wrappedPlainText() + wrappedText(Link) + wrappedPlainText(space + plainText())
            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }

        generate {
            val items = listOf(parsedText(Link), parsedPlaintText(space + plainText()), parsedText(Link))
            val headingText = wrappedText(Link) + wrappedPlainText(space + plainText()) + wrappedText(Link)
            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }

        generate {
            val items = listOf(parsedText(Link), parsedText(Link))
            val headingText = wrappedText(Link) + wrappedText(Link)
            parse(wrappedText(Heading, headingText)) {
                match(PageTextHeading(headingText, items))
            }
        }
    }
}
