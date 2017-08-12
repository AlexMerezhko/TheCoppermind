package com.thecoppermind.Robots

import com.google.gson.GsonBuilder
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageData
import com.thecoppermind.page.PageTextInterface
import org.junit.Assert.assertTrue
import kotlin.properties.Delegates


fun page(func: PageRobot.() -> Unit) = PageRobot().apply(func)

class PageRobot {
    var page by Delegates.notNull<PageData>()

    var id by Delegates.notNull<Int>()
    var title by Delegates.notNull<String>()

    fun fromGeneratedResponse(id: Int, title: String, text: String) {
        this.id = id
        this.title = title
        fromResponse(DataGeneratorForTests().jsonResponse(id, title, text)) // TODO
    }

    fun fromResponse(response: String) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(PageData::class.java, PageClassDeserializer());
        val gson = gsonBuilder.create()
        page = gson.fromJson(response, PageData::class.java)
    }

    fun match(vararg texts: PageTextInterface) {
        matchHeader(id, title)
        matchContent(texts)
    }

    fun match(id: Int = this.id, title: String = this.title, vararg texts: PageTextInterface) {
        matchHeader(id, title)
        matchContent(texts)
    }

    fun matchHeader(id: Int, title: String) {
        headers { match(page, id, title) }
    }

    fun matchContent(texts: Array<out PageTextInterface>) {
        content { match(page.parts, texts) }
    }
}

fun headers(func: PageHeadersRobot.() -> Unit) = PageHeadersRobot().apply(func)
class PageHeadersRobot {

    fun match(page: PageData, id: Int, title: String) {
        assertTrue(page.id == id)
        assertTrue(page.title == title)
    }
}
