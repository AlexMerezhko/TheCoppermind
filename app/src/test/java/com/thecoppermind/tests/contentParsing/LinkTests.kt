@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.contentParsing

import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.Link
import com.thecoppermind.page.PageTextLink
import com.thecoppermind.page.PageTextPlain
import com.thecoppermind.robots.generate
import com.thecoppermind.robots.parse
import com.thecoppermind.utils.getIdForLink
import com.thecoppermind.utils.getTextWithEnumeration
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkTests {

    @Test
    fun `one link`() {

        val linkText = "This is Link"
        val linkId = "This_is_Link"
//        val linkHeading = "to the heading"

        parse("[[$linkText]]") {
            match(PageTextLink(linkText, linkId))
        }
    }

    @Test
    fun `empty link`() {
        generate {
            parse(wrappedText(Link, " ")) {
                matchResultEmpty()
            }
            parse(wrappedText(Link, "  ")) {
                matchResultEmpty()
            }
        }
    }

    @Test
    fun `two links in a row`() {
        generate {
            parse(wrappedText(Link) + wrappedText(Link)) {
                match(parsedText(Link), parsedText(Link))
            }
        }
    }

    @Test
    fun `create linkId from text`() {
        assertTrue("id".getIdForLink() == "id")
        assertTrue("more than one word".getIdForLink() == "more_than_one_word")
        assertTrue("few_underlines in text".getIdForLink() == "few_underlines_in_text")
        assertTrue("all_underlines_in_text".getIdForLink() == "all_underlines_in_text")
    }

    @Test
    fun `removing spaces in created link id`() {
        assertTrue(" spaces ".getIdForLink() == "spaces")
        assertTrue("  spaces  ".getIdForLink() == "spaces")
        assertTrue(" spaces before and after words ".getIdForLink() == "spaces_before_and_after_words")
        assertTrue(" spaces_before_and_after_words_with_underlines ".getIdForLink() == "spaces_before_and_after_words_with_underlines")
    }

    @Test
    fun `link with different text to show`() {
        val pageId = "Real page id"
        val linkText = "link to show"
        generate {
            parse(wrappedText(Link, "$pageId|$linkText")) {
                match(PageTextLink(linkText, pageId.getIdForLink()))
            }
        }
    }

    @Test
    fun `link with scroll to heading`() {
        val pageId = "Real page id"
        val heading = "Heading to scroll"
        val linkText = "link to show"
        generate {
            parse(wrappedText(Link, "$pageId#$heading|$linkText")) {
                match(PageTextLink(linkText, pageId.getIdForLink(), heading))
            }
            parse(wrappedText(Link, "$pageId#|$linkText")) {
                match(PageTextLink(linkText, pageId.getIdForLink()))
            }
        }
    }

    @Test
    fun `with postfix`() {
        generate {
            val linkId = textForType(Link).getIdForLink()
            val postfix = "postfix"
            val notPostfix = plainText()

            // текст без разделителей до окончания строки
            parse(wrappedText(Link) + postfix) {
                match(PageTextLink(textForType(Link) + postfix, linkId))
            }

            // разделители
            for (divider in listOf(' ', ',', '.')) {
                parse(wrappedText(Link) + postfix + divider + notPostfix) {
                    match(PageTextLink(textForType(Link) + postfix, linkId), PageTextPlain(divider + notPostfix))
                }
            }

            // начала перечислений
            for (divider in listOf('*', '#')) {
                parse(wrappedText(Link) + postfix + divider + notPostfix) {
                    match(PageTextLink(textForType(Link) + postfix, linkId), PageTextPlain((divider + notPostfix).getTextWithEnumeration()))
                }
            }
            // перенос строки
            for (divider in listOf('\n')) {
                parse(wrappedText(Link) + postfix + divider + notPostfix) {
                    match(PageTextLink(textForType(Link) + postfix, linkId), PageTextPlain(divider + notPostfix))
                }
            }
            // другие типы текста
            for (type in PageClassDeserializer.Companion.notPlainTextVariants) {
                parse(wrappedText(Link) + postfix + wrappedText(type)) {
                    match(PageTextLink(textForType(Link) + postfix, linkId), parsedText(type))
                }
            }
        }
    }
}
