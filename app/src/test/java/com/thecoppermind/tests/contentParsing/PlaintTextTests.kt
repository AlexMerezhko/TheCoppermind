@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Heading
import com.thecoppermind.page.PageTextPlain
import com.thecoppermind.robots.generate
import com.thecoppermind.robots.parse
import com.thecoppermind.utils.getTextWithEnumeration
import org.junit.Assert
import org.junit.Test

class PlaintTextTests {

    @Test
    fun `only normal text`() {
        val plaintText = "This is normal text"
        parse(plaintText) {
            match(PageTextPlain(plaintText))
        }
    }

    @Test
    fun `empty text`() {
        generate {
            parse(wrappedText(Heading, " ")){
                matchResultEmpty()
            }
            parse(wrappedText(Heading, "  ")){
                matchResultEmpty()
            }
        }
    }
    @Test
    fun `removing new lines before heading`() {
        generate {
            parse(wrappedPlainText() + '\n' + wrappedText(Heading)) {
                match(parsedPlaintText(), parsedText(Heading))
            }

            parse(wrappedPlainText() + '\n' + '\n' + wrappedText(Heading)) {
                match(parsedPlaintText(), parsedText(Heading))
            }
        }
    }

    @Test
    fun `check update enumerations in Text`() {

        generate {
            val baseText = plainText()

            Assert.assertTrue(("" + baseText).getTextWithEnumeration() == baseText)

            Assert.assertTrue(("*" + baseText).getTextWithEnumeration() == " - " + baseText)
            Assert.assertTrue(("*$baseText\n*$baseText").getTextWithEnumeration() == " - $baseText\n - $baseText")
            Assert.assertTrue((" * $baseText").getTextWithEnumeration() == "  -  $baseText")

            Assert.assertTrue(("#" + baseText).getTextWithEnumeration() == " - " + baseText)
            Assert.assertTrue(("#$baseText\n#$baseText").getTextWithEnumeration() == " - $baseText\n - $baseText")
            Assert.assertTrue((" # $baseText").getTextWithEnumeration() == "  -  $baseText")

            val result = " - "
            for (symbol in listOf("*", "#")) {
                parse(symbol + baseText) {
                    match(PageTextPlain(result + baseText))
                }
            }
        }
    }
}
