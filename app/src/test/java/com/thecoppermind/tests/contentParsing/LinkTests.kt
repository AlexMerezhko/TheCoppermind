@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextLink
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageTextNormal
import com.thecoppermind.utils.getIdForLink
import com.thecoppermind.utils.getTextWithEnumeration
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Link

class LinkTests {

    @Test
    fun `check generated data`() {
        generator {
            val linkText = exampleForContentType(Link)
            Assert.assertTrue("[[$linkText]]" == exampleForContentTypeWithBorders(Link))
            Assert.assertTrue(PageTextLink(linkText, linkText.getIdForLink()) == exampleForParsedContentType(Link))

            content {
                text(exampleForContentTypeWithBorders(Link))
                match(exampleForParsedContentType(Link))
            }
        }
    }

    @Test
    fun `no links`() {
        generator {
            content {
                text(generateDataForAllTypesExceptOne(PageTextLink::class))
                notContains(PageTextLink::class)
            }
        }
    }

    @Test
    fun `empty link`() {
        generator {
            content {
                text(wrapWithContentTypeBorders(Link, ""))
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `one link`() {
        content {
            text("[[This is link]]")
            match(PageTextLink("This is link", "This_is_link"))
        }
    }

    @Test
    fun `two links in a row`() {
        content {
            text("[[This is first link]][[This is second link]]")
            match(PageTextLink("This is first link", "This_is_first_link"), PageTextLink("This is second link", "This_is_second_link"))
        }
    }

    @Test
    fun `link with different text to show`() {
        content {
            val pageId = "Real page id"
            val linkText = "link to show"

            text("[[$pageId|$linkText]]")
            match(PageTextLink(linkText, pageId.getIdForLink()))
        }
    }

    @Test
    fun `link with scroll to content`() {
        content {
            val pageId = "Real page id"
            val heading = "Heading to scroll"
            val linkText = "link to show"

            text("[[$pageId#$heading|$linkText]]")
            match(PageTextLink(linkText, pageId.getIdForLink(), heading))
        }
    }

    @Test
    fun `create Id for Link from Text`() {
        assertTrue("This is link".getIdForLink() == "This_is_link")
        assertTrue("ThisIsLink".getIdForLink() == "ThisIsLink")
        assertTrue(" This is link ".getIdForLink() == "This_is_link")
    }

    @Test
    fun `with postfix`() {
        val baseText = "This is link"
        val linkText = baseText
        val linkId = linkText.getIdForLink()
        val postfix: String = "postfix"
        var notPostfix: String = "afterPostfix"

        // текст без разделителей до окончания строки
        content {
            text("[[$baseText]]$postfix")
            match(PageTextLink(linkText + postfix, linkId))
        }

        // разделители
        for (divider in listOf(' ', ',', '.')) {
            content {
                text("[[$baseText]]$postfix$divider$notPostfix")
                match(PageTextLink(linkText + postfix, linkId), PageTextNormal(divider + notPostfix))
            }
        }

        // начала перечислений
        for (divider in listOf('*', '#')) {
            content {
                text("[[$baseText]]$postfix$divider$notPostfix")
                match(PageTextLink(linkText + postfix, linkId), PageTextNormal((divider + notPostfix).getTextWithEnumeration()))
            }
        }
        // перенос строки
        for (divider in listOf('\n')) {
            content {
                text("[[$baseText]]$postfix$divider$notPostfix")
                match(PageTextLink(linkText + postfix, linkId), PageTextNormal(divider + notPostfix))
            }
        }
        // другие типы текста
        for (type in PageClassDeserializer.Companion.notNormalTextVariants) {
            generator {
                notPostfix = exampleForContentTypeWithBorders((type))
                content {
                    text("[[$baseText]]$postfix" + exampleForContentTypeWithBorders((type)))
                    match(PageTextLink(linkText + postfix, linkId), exampleForParsedContentType(type))
                }
            }
        }
    }
}