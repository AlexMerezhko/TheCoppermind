package com.thecoppermind.Robots

import com.thecoppermind.page.*
import com.thecoppermind.utils.getIdForLink
import kotlin.reflect.KClass

fun generator(func: DataGeneratorForTests.() -> Unit) = DataGeneratorForTests().apply(func)
class DataGeneratorForTests {

    // ----- все типы текстовох блоково------

    fun getAllTypesOfItems() = listOf(PageTextNormal::class, PageTextBold::class, PageTextHeading::class, PageTextTemplate::class, PageTextHeading::class)

    // ----- основной формат ответа ------

    fun generateJsonResponse(id: Int, title: String, text: String) = "{\"parse\":{\"title\":\"$title\",\"pageid\":$id,\"wikitext\":{\"*\":\"$text\"}}}"

    // ----- создание текстовых элементов - частей необработанных данных ------

    fun generateDataForNormalText() = "This is normal text"
    fun generateDataForContentType(type: PageClassDeserializer.Companion.ContentType): String {
        return "This is ${type.name} text"
    }

    fun generateDataForAllTypes(): String {
        return generateDataForAllTypesExceptOne(null)
    }

    fun generateDataForAllTypesExceptOne(clazz: KClass<out PageTextInterface>?): String {
        var result = ""

        if (clazz != PageTextNormal::class) result += generateDataForNormalText()
        if (clazz != PageTextHeading::class) result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Heading)
        if (clazz != PageTextLink::class) result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Link)
        if (clazz != PageTextTemplate::class) result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Template)
        if (clazz != PageTextBold::class) result += wrapDataForContentType(PageClassDeserializer.Companion.ContentType.Bold)

        return result
    }

    fun wrapDataForContentType(type: PageClassDeserializer.Companion.ContentType, baseText: String = generateDataForContentType(type)): String {
        var text: String = ""
        for (i in 0 until type.charsCountInRow) text += type.startChar
        text += baseText
        for (i in 0 until type.charsCountInRow) text += type.endChar
        return text
    }

    // ----- создание текстовых элементов - обработанных данных ------

    fun generateParsedNormalText() = PageTextNormal(generateDataForNormalText())
    fun generateParsedTextForContent(type: PageClassDeserializer.Companion.ContentType): PageTextInterface {
        when (type) {
            PageClassDeserializer.Companion.ContentType.Bold -> return PageTextBold(generateDataForContentType(type))
            PageClassDeserializer.Companion.ContentType.Link -> {
                val text = generateDataForContentType(type)
                return PageTextLink(text, text.getIdForLink())
            }
            PageClassDeserializer.Companion.ContentType.Template -> return PageTextTemplate(generateDataForContentType(type))
            PageClassDeserializer.Companion.ContentType.Heading -> return PageTextHeading(generateDataForContentType(type))
        }
    }

    fun generateParsedAllTypes(): Array<out PageTextInterface> {
        return arrayOf(
                generateParsedNormalText(),
                generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Bold),
                generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Link),
                generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Template),
                generateParsedTextForContent(PageClassDeserializer.Companion.ContentType.Heading)
        )
    }
}
