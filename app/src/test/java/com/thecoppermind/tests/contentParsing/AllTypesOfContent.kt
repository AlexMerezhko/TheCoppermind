@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.*
import org.junit.Test


class AllTypesOfContent {

    @Test
    fun `no content`() {
        generator {
            content {
                text("")
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `all types of content`() {
        generator {
            content {
//                text(generateDataForAllTypes())
//                match(generateParsedAllTypes())
                var result = ""
                result += generateDataForNormalText()
                result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Heading)
                result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Link)
                result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Template)
                result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Bold)

                text(result)
                match(
                        generateParsedNormalText(),
                        generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Heading),
                        generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Link),
                        generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Template),
                        generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Bold)
                )
            }
        }
    }

    @Test
    fun `all content without one`() {
        generator {
            for (content in getAllTypesOfItems()) {
                content {
                    text(generateDataForAllTypesExceptOne(content))
                    notContains(content)
                }
            }
        }
    }

    @Test
    fun `empty bold`() {
        generator {
            content {
                text(wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Bold, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one bold text`() {
        generator {
            content {
                text(wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Bold))
                match(PageTextBold(generateDataForContentType(PageClassDeserializer.Companion.ContentType.Bold)))
            }
        }
    }
}
