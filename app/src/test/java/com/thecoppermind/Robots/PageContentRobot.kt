package com.thecoppermind.Robots

import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextInterface
import org.junit.Assert.assertTrue
import kotlin.properties.Delegates

fun content(func: PageContentRobot.() -> Unit) = PageContentRobot().apply(func)
class PageContentRobot {

    var parts by Delegates.notNull<ArrayList<PageTextInterface>>()

    fun text(rawText: String) {
        parts = PageClassDeserializer.getPageContent(rawText.toCharArray())
    }

    fun matchResultEmpty() {
        assertTrue(parts.size == 0)
    }

    fun match(vararg texts: PageTextInterface) {
        match(parts, texts)
    }
//
//    fun match(texts : Array<out PageTextInterface>) {
//        match(parts, texts)
//    }

    fun match(parts: ArrayList<PageTextInterface>, texts: Array<out PageTextInterface>) {
        assertTrue(parts.size == texts.size && parts.indices.none { parts[it] != texts[it] })
    }

    fun notContains(clazz: Any) {
        assertTrue(parts.indices.none { clazz == parts[it]::class })
    }

    inline fun <reified T> notContains() {
        assertTrue(parts.indices.none { parts[it] is T })
    }
}


