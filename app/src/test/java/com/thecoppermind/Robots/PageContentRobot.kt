package com.thecoppermind.robots

import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageTextInterface
import org.junit.Assert.assertTrue

fun parse(parts: ArrayList<PageTextInterface>, func: PageContentRobot.() -> Unit) = PageContentRobot(parts).apply(func)
fun parse(rawText: String, func: PageContentRobot.() -> Unit) = PageContentRobot(rawText).apply(func)

class PageContentRobot(val parts : ArrayList<PageTextInterface>) {

    constructor(rawText: String) : this(PageClassDeserializer.getPageContent(rawText.toCharArray()))

//    constructor(rawText: String) : this(val parts1 : ArrayList<PageTextInterface>){
//        parts1 = PageClassDeserializer.getPageContent(rawText.toCharArray())
//    }

//    constructor(rawText: String) : this(){
//        PageClassDeserializer.getPageContent(rawText.toCharArray())
//    }

    fun matchResultEmpty() {
        match()
    }

    fun match(vararg texts: PageTextInterface) {
        assertTrue(parts.size == texts.size && parts.indices.none { parts[it] != texts[it] })
    }

//    fun match(vararg texts: Array<out PageTextInterface>) {
//        assertTrue(parts.size == texts.size && parts.indices.none { parts[it] != texts[it] })
//    }

    fun match(texts: ArrayList<PageTextInterface>) {
        assertTrue(parts.size == texts.size && parts.indices.none { parts[it] != texts[it] })
    }

    fun notContains(clazz: Any) {
        assertTrue(parts.indices.none { clazz == parts[it]::class })
    }

    inline fun <reified T> notContains() {
        assertTrue(parts.indices.none { parts[it] is T })
    }
}
