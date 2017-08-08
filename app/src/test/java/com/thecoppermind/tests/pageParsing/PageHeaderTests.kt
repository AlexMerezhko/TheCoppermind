@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.pageParsing

import com.thecoppermind.Robots.page
import com.thecoppermind.page.PageTextNormal
import org.junit.Test


class PageHeaderTests {

    @Test
    fun `empty body`() {
        page {
            fromGeneratedResponse(390, "The Final Empire", "")
            match()
        }
    }

    @Test
    fun `one normal text`() {
        page {
            fromGeneratedResponse(390, "The Final Empire", "some amount of text")
            match(PageTextNormal("some amount of text"))
        }
    }

    @Test
    fun `no title`() {
        page {
            fromGeneratedResponse(390, "", "some amount of text")
            match(390, "", PageTextNormal("some amount of text"))
        }
    }

    @Test
    fun `no id`() {
        page {
            fromGeneratedResponse(0, "The Final Empire", "some amount of text")
            match(0, "The Final Empire", PageTextNormal("some amount of text"))
        }
    }
}