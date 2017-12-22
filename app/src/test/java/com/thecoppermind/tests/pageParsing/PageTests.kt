@file:Suppress("IllegalIdentifier")

package com.thecoppermind.tests.pageParsing

import com.thecoppermind.page.PageTextPlain
import com.thecoppermind.robots.generate
import com.thecoppermind.robots.verifyPage
import org.junit.Test


class PageTests {

    @Test
    fun `empty body`() {
        generate {
            verifyPage(generatedResponse(390, "The Final Empire", "")) {
                matchAll(390, "The Final Empire")
            }
        }
    }

    @Test
    fun `one normal text`() {
        generate {
            verifyPage(generatedResponse(390, "The Final Empire", "some amount of text")) {
                matchAll(390, "The Final Empire", PageTextPlain("some amount of text"))
            }
        }
    }

    // it's a server problem
//    @Test
//    fun `no title`() {
//        generate {
//            verifyPage(generatedResponse(390, "some amount of text")) {
//                matchAll(390, "", PageTextNormal("some amount of text"))
//            }
//        }
//    }

    // it's a server problem
//    @Test
//    fun `no id`() {
//        generate {
//            verifyPage(generatedResponse("The Final Empire", "some amount of text")) {
//                matchAll(0, "The Final Empire", PageTextNormal("some amount of text"))
//            }
//        }
//    }
}
