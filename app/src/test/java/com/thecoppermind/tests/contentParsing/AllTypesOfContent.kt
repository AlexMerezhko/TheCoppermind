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
                result += exampleNormalText()
                result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Heading)
                result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Link)
                result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Template)
                result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.BoldItalic)

                text(result)
                match(
                        exampleParsedNormalText(),
                        exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Heading),
                        exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Link),
                        exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Template),
                        exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.BoldItalic)
                )
            }
        }
    }

    @Test
    fun `all content without one`() {
        generator {
            for (content in allTypesOfItems()) {
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
                text(wrapWithContentTypeBorders(PageClassDeserializer.Companion.ContentType.BoldItalic, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one bold text`() {
        generator {
            content {
                text(exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.BoldItalic))
                match(PageTextBoldItalic(exampleForContentType(PageClassDeserializer.Companion.ContentType.BoldItalic)))
            }
        }
    }
}
