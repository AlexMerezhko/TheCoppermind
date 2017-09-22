package com.thecoppermind.Robots

import com.thecoppermind.page.*
import com.thecoppermind.utils.getIdForLink
import kotlin.reflect.KClass

fun generator(func: DataGeneratorForTests.() -> Unit) = DataGeneratorForTests().apply(func)
class DataGeneratorForTests {

    // ----- все типы текстовох блоково------

    fun allTypesOfItems() = listOf(PageTextNormal::class, PageTextBoldItalic::class, PageTextHeading::class, PageTextTemplate::class, PageTextHeading::class)

    // ----- основной формат ответа ------

    fun jsonResponse(id: Int, title: String, text: String) = "{\"parse\":{\"title\":\"$title\",\"pageid\":$id,\"wikitext\":{\"*\":\"$text\"}}}"

    // ----- создание текстовых элементов - частей необработанных данных ------

    fun exampleNormalText() = "This is normal text"
    fun exampleForContentType(type: PageClassDeserializer.Companion.ContentType): String {
        return "This is ${type.name} text"
    }

//    fun generateDataForAllTypes(): String {
//        return generateDataForAllTypesExceptOne(null)
//    }

    fun generateDataForAllTypesExceptOne(clazz: KClass<out PageTextInterface>?): String {
        var result = ""

        if (clazz != PageTextNormal::class) result += exampleNormalText()
        if (clazz != PageTextHeading::class) result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Heading)
        if (clazz != PageTextLink::class) result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Link)
        if (clazz != PageTextTemplate::class) result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.Template)
        if (clazz != PageTextBoldItalic::class) result += exampleForContentTypeWithBorders(PageClassDeserializer.Companion.ContentType.BoldItalic)

        return result
    }

    fun exampleForContentTypeWithBorders(type: PageClassDeserializer.Companion.ContentType): String {
        return wrapWithContentTypeBorders(type)
    }

//    fun exampleLinkWithScroll(): String {
//        return "[[Real page id#Heading to scroll|link to show]]"
//    }

    fun wrapWithContentTypeBorders(type: PageClassDeserializer.Companion.ContentType, baseText: String = exampleForContentType(type)): String {
        var text: String = ""
        for (i in 0 until type.charsCountInRow) text += type.startChar
        text += baseText
        for (i in 0 until type.charsCountInRow) text += type.endChar
        return text
    }

    // ----- создание текстовых элементов - обработанных данных ------

    fun exampleParsedNormalText() = PageTextNormal(exampleNormalText())
    fun exampleForParsedContentType(type: PageClassDeserializer.Companion.ContentType): PageTextInterface {
        when (type) {
            PageClassDeserializer.Companion.ContentType.BoldItalic -> return PageTextBoldItalic(exampleForContentType(type))
            PageClassDeserializer.Companion.ContentType.Link -> {
                val text = exampleForContentType(type)
                return PageTextLink(text, text.getIdForLink())
            }
            PageClassDeserializer.Companion.ContentType.Template -> return PageTextTemplate(exampleForContentType(type))
            PageClassDeserializer.Companion.ContentType.Heading -> return PageTextHeading(exampleForContentType(type))
        }
    }

//    fun generateParsedAllTypes(): Array<out PageTextInterface> {
//        return arrayOf(
//                exampleParsedNormalText(),
//                exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Bold),
//                exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Link),
//                exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Template),
//                exampleForParsedContentType(PageClassDeserializer.Companion.ContentType.Heading)
//        )
//    }
}
