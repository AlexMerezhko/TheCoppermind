@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Heading
import com.thecoppermind.page.PageTextNormal
import com.thecoppermind.utils.getTextWithEnumeration
import org.junit.Assert
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

    @Test
    fun `only normal text`() {
        content {
            text("This is normal text")
            match(PageTextNormal("This is normal text"))
        }

        generator {
            content {
                text(exampleNormalText())
                match(exampleParsedNormalText())
            }
        }
    }

    @Test
    fun `removing new lines before heading`() {
        generator {
            content {
                text(exampleNormalText() + '\n' + exampleForContentTypeWithBorders(Heading))
                match(exampleParsedNormalText(), exampleForParsedContentType(Heading))
            }

            content {
                text(exampleNormalText() + '\n' + '\n' + exampleForContentTypeWithBorders(Heading))
                match(exampleParsedNormalText(), exampleForParsedContentType(Heading))
            }
        }
    }

    @Test
    fun `check update enumerations in Text`() {

        Assert.assertTrue("This is normal text".getTextWithEnumeration() == "This is normal text")

        Assert.assertTrue("*This is normal text".getTextWithEnumeration() == " - This is normal text")
        Assert.assertTrue(("*This is normal text" + "*This is normal text").getTextWithEnumeration() == " - This is normal text" + " - This is normal text")
        Assert.assertTrue(" * This is normal text".getTextWithEnumeration() == "  -  This is normal text")

        Assert.assertTrue("#This is normal text".getTextWithEnumeration() == " - This is normal text")
        Assert.assertTrue(("#This is normal text" + "#This is normal text").getTextWithEnumeration() == " - This is normal text" + " - This is normal text")
        Assert.assertTrue(" # This is normal text".getTextWithEnumeration() == "  -  This is normal text")

        generator {
            val baseText = exampleNormalText()
            val result = " - "

            for (symbol in listOf("*", "#")) {
                content {
                    text(symbol + baseText)
                    match(PageTextNormal(result + baseText))
                }
            }
        }
    }
}
