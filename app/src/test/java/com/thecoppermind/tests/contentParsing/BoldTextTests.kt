@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextBold
import com.thecoppermind.Robots.generator
import org.junit.Test


class BoldTextTests {

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
