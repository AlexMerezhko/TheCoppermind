@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.Robots.content
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextLink
import com.thecoppermind.Robots.generator
import com.thecoppermind.page.PageTextNormal
import com.thecoppermind.utils.getIdForLink
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkTests {

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
                text(wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Link, ""))
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
    fun `create Id for Link from Text`() {
        assertTrue("This is link".getIdForLink() ==  "This_is_link")
        assertTrue("ThisIsLink".getIdForLink() ==  "ThisIsLink")
        assertTrue(" This is link ".getIdForLink() ==  "This_is_link")
    }

    @Test
    fun `with postfix`() {
        val baseText = "This is link"
        val linkText = baseText
        val linkId = linkText.getIdForLink()
        var postfix: String = "postfix"
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
                match(PageTextLink(linkText + postfix, linkId), PageTextNormal(" - " + notPostfix))
            }
        }
        // перенос строки
        for (divider in listOf('\n')) {
            content {
                text("[[$baseText]]$postfix$divider$notPostfix")
                match(PageTextLink(linkText + postfix, linkId), PageTextNormal(notPostfix))
            }
        }
        // другие типы текста
        for (type in PageClassDeserializer.Companion.notNormalTextVariants) {
            generator {
                notPostfix = wrapDataForContentType((type))
                content {
                    text("[[$baseText]]$postfix" + wrapDataForContentType((type)))
                    match(PageTextLink(linkText + postfix, linkId), generateParsedTextForContent(type))
                }
            }
        }
    }
}