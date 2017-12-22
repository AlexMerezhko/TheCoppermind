@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests

import com.thecoppermind.page.*
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.*
import com.thecoppermind.robots.generate
import com.thecoppermind.robots.parse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratorTests {

    @Test
    fun `no content`() {
        parse("") {
            match()
        }
    }


    @Test
    fun `check generated plain text`() {
        generate {
            val plaintText = plainText()
            assertTrue(plaintText == wrappedPlainText())
            assertTrue(PageTextPlain(text = plaintText) == parsedPlaintText())

            parse(wrappedPlainText()) {
                match(parsedPlaintText())
            }
        }
    }

    @Test
    fun `check generated headers`() {
        generate {
            val headingText = textForType(Heading)
            assertTrue("==$headingText==" == wrappedText(Heading))
            assertTrue("==$headingText==" == wrappedText(type = Heading, headingLevel = HeadingLevel.H2))
            assertTrue("===$headingText===" == wrappedText(type = Heading, headingLevel = HeadingLevel.H3))
            assertTrue("====$headingText====" == wrappedText(type = Heading, headingLevel = HeadingLevel.H4))
            assertTrue("=====$headingText=====" == wrappedText(type = Heading, headingLevel = HeadingLevel.H5))
            assertTrue("======$headingText======" == wrappedText(type = Heading, headingLevel = HeadingLevel.H6))
            assertTrue(PageTextHeading(text = headingText) == parsedText(Heading))
            assertTrue(PageTextHeading(text = headingText, level = HeadingLevel.H2) == parsedText(type = Heading, headingLevel = HeadingLevel.H2))
            assertTrue(PageTextHeading(text = headingText, level = HeadingLevel.H3) == parsedText(type = Heading, headingLevel = HeadingLevel.H3))
            assertTrue(PageTextHeading(text = headingText, level = HeadingLevel.H4) == parsedText(type = Heading, headingLevel = HeadingLevel.H4))
            assertTrue(PageTextHeading(text = headingText, level = HeadingLevel.H5) == parsedText(type = Heading, headingLevel = HeadingLevel.H5))
            assertTrue(PageTextHeading(text = headingText, level = HeadingLevel.H6) == parsedText(type = Heading, headingLevel = HeadingLevel.H6))

            parse(wrappedText(Heading)) {
                match(parsedText(Heading))
            }
        }
    }
    @Test
    fun `all raw texts in line`() {

        generate {
            var source = ""

            source += wrappedPlainText()
            for (value in HeadingLevel.values()) {
                source += wrappedText(type = Heading, headingLevel = value)
            }
            for (value in BoldItalicType.values()) {
                source += wrappedText(type = BoldItalic, boldItalicType = value)
            }
            source += wrappedText(Link)
            source += wrappedText(Template)
            assertTrue(source == allWrappedTypes())
        }
    }

    @Test
    fun `all raw texts except one`() {
        generate {
            for (content in allTypesOfItems()) {
                parse(allWrappedTypesExceptOne(content)) {
                    notContains(content)
                }
            }
        }
    }

    @Test
    fun `list of parsed items`() {

        generate {
            val result = ArrayList<PageTextInterface>()

            result.add(parsedPlaintText())
            result.addAll(Array(HeadingLevel.values().size, { i -> parsedText(type = Heading, headingLevel = HeadingLevel.values()[i]) }))
            result.addAll(Array(BoldItalicType.values().size, { i -> parsedText(type = BoldItalic, boldItalicType = BoldItalicType.values()[i]) }))
            result.add(parsedText(Link))
            result.add(parsedText(Template))

            assertTrue(result == allParsedTypes())
        }
    }

    @Test
    fun `all parsed items except one`() {
        generate {
            for (content in allTypesOfItems()) {
                parse(allParsedTypesExceptOne(content)) {
                    notContains(content)
                }
            }
        }
    }

    @Test
    fun `all types of content`() {
        generate {
            parse(allWrappedTypes()) {
                match(allParsedTypes())
            }
        }
    }
}