package com.thecoppermind.robots

import com.google.gson.GsonBuilder
import com.thecoppermind.page.PageClassDeserializer
import com.thecoppermind.page.PageData
import com.thecoppermind.page.PageTextInterface
import org.junit.Assert.assertTrue

fun verifyPage(page: PageData, func: PageRobot.() -> Unit) = PageRobot(page).apply(func)
//fun verifyPage(id: Int, title: String, text: String, func: PageRobot.() -> Unit) = PageRobot(id, title, text).apply(func)

class PageRobot {

    val page: PageData

    constructor(page: PageData) {
        this.page = page
    }

//    constructor(id: Int, title: String, text: String) {
//        val gsonBuilder = GsonBuilder()
//        gsonBuilder.registerTypeAdapter(PageData::class.java, PageClassDeserializer()).create()
//        val gson = gsonBuilder.create()
//        val response = DataGenerator().jsonResponse(id, title, text);
//        page = gson.fromJson(response, PageData::class.java)
//    }

    fun matchAll(id: Int, title: String, vararg texts: PageTextInterface) {
        matchHeaders(id, title)
        matchContent(ArrayList<PageTextInterface>(texts.asList()))
    }

    fun matchHeaders(id: Int, title: String){
        assertTrue(page.id == id)
        assertTrue(page.title == title)
    }

//    fun matchContent(vararg texts: PageTextInterface) {
//        matchContent(ArrayList<PageTextInterface>(texts.asList()))
//    }

    fun matchContent(texts: ArrayList<PageTextInterface>) {
        parse(page.parts) {
            match(texts)
        }
    }
}

//fun verifyHeaders(func: PageHeadersRobot.() -> Unit) = PageHeadersRobot().apply(func)
//class PageHeadersRobot {
//
//    fun match(page: PageData, id: Int, title: String) {
//        assertTrue(page.id == id)
//        assertTrue(page.title == title)
//    }
//}
