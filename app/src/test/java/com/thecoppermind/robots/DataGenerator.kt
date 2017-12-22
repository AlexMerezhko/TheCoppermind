package com.thecoppermind.robots

import com.google.gson.GsonBuilder
import com.thecoppermind.page.*
import com.thecoppermind.utils.getIdForLink
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType
import com.thecoppermind.page.PageClassDeserializer.Companion.ContentType.*
import kotlin.reflect.KClass


fun generate(func: DataGenerator.() -> Unit) = DataGenerator().apply(func)
class DataGenerator {

    // ----- основной формат ответа ------

        private fun jsonResponse(id: Int, title: String, text: String) = "{\"parse\":{\"title\":\"$title\",\"pageid\":$id,\"wikitext\":{\"*\":\"$text\"}}}"
//    private fun jsonResponse(id: Int?, title: String?, text: String?): String {
//
//        if (id != null && title != null && text != null) {
//            return "{\"parse\":{\"title\":\"$title\",\"pageid\":$id,\"wikitext\":{\"*\":\"$text\"}}}"
//        }
//
//        if (id == null && title != null && text != null) {
//            return "{\"parse\":{\"title\":\"$title\",\"wikitext\":{\"*\":\"$text\"}}}"
//        }
//
//        if (id != null && title == null && text != null) {
//            return "{\"parse\":{\"pageid\":$id,\"wikitext\":{\"*\":\"$text\"}}}"
//        }
//
//        if (id == null && title == null && text != null) {
//            return "{\"parse\":{\"wikitext\":{\"*\":\"$text\"}}}"
//        }
//    }

    fun generatedResponse(id: Int, title: String, text: String): PageData {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(PageData::class.java, PageClassDeserializer()).create()
        val gson = gsonBuilder.create()
        val response = DataGenerator().jsonResponse(id, title, text);
        return gson.fromJson(response, PageData::class.java)
    }

    // ----- тексты внутри блоков ------

    fun plainText() = "This is normal content"

    fun textForType(type: ContentType): String {
        return "This is ${type.name} content"
    }

    // ----- создание текстовых строк - необработанных данных ------

    fun linkWithScroll(): String {
        return "[[Real page id#Heading to scroll|link to show]]"
    }

    fun wrappedPlainText(baseText: String = plainText()) : String{
        return baseText
    }

    fun wrappedText(
            type: ContentType,
            baseText: String = textForType(type),
            boldItalicType: BoldItalicType = BoldItalicType.Bold,
            headingLevel: HeadingLevel = HeadingLevel.H2
    ): String {
        var text = ""
        when (type) {
            BoldItalic -> {
                for (i in 0 until boldItalicType.countOfBorderChars) text += type.startChar
                text += baseText
                for (i in 0 until boldItalicType.countOfBorderChars) text += type.endChar
                return text
            }
            Heading -> {
                for (i in 0 until headingLevel.countOfBorderChars) text += type.startChar
                text += baseText
                for (i in 0 until headingLevel.countOfBorderChars) text += type.endChar
                return text
            }
            else -> {
                for (i in 0 until type.charsCountInRow) text += type.startChar
                text += baseText
                for (i in 0 until type.charsCountInRow) text += type.endChar
                return text
            }
        }
    }

    // ----- создание элементов - обработанных данных ------

    fun parsedPlaintText(baseText: String = plainText()): PageTextInterface {
        return PageTextPlain(baseText)
    }

    fun parsedText(
            type: ContentType,
            baseText: String = textForType(type),
            boldItalicType: BoldItalicType = BoldItalicType.Bold,
            headingLevel: HeadingLevel = HeadingLevel.H2
    ): PageTextInterface {
        return when (type) {
            BoldItalic -> PageTextBoldItalic(baseText, boldItalicType)
            Link -> {
                PageTextLink(baseText, baseText.getIdForLink())
            }
            Template -> PageTextTemplate(baseText)
            Heading -> PageTextHeading(text = baseText, level = headingLevel)
        }
    }

    // ----- все типы блоков ------

    fun allTypesOfItems() = listOf(PageTextPlain::class, PageTextHeading::class, PageTextBoldItalic::class, PageTextLink::class, PageTextTemplate::class)

    fun allWrappedTypes(): String {
        return allWrappedTypesExceptOne(null)
    }

    fun allWrappedTypesExceptOne(exceptedClass: KClass<out PageTextInterface>?): String {
        var result = ""
        allTypesOfItems()
                .asSequence()
                .filter { it != exceptedClass }
                .forEach {
                    when (it) {
                        PageTextPlain::class -> result += plainText()
                        PageTextHeading::class -> {
                            for (value in HeadingLevel.values()) {
                                result += wrappedText(type = Heading, headingLevel = value)
                            }
                        }
                        PageTextBoldItalic::class -> {
                            for (value in BoldItalicType.values()) {
                                result += wrappedText(type = BoldItalic, boldItalicType = value)
                            }
                        }
                        PageTextLink::class -> result += wrappedText(Link)
                        PageTextTemplate::class -> result += wrappedText(Template)
                    }
                }
        return result
    }

    fun allParsedTypes(): ArrayList<PageTextInterface> {
        return allParsedTypesExceptOne(null)
    }

    fun allParsedTypesExceptOne(clazz: KClass<out PageTextInterface>?): ArrayList<PageTextInterface> {
        val result = ArrayList<PageTextInterface>()

        if (clazz != PageTextPlain::class) result.add(parsedPlaintText())
        if (clazz != PageTextHeading::class) {
            HeadingLevel.values().mapTo(result) { parsedText(type = Heading, headingLevel = it) }
        }
        if (clazz != PageTextBoldItalic::class) {
            BoldItalicType.values().mapTo(result) { parsedText(type = BoldItalic, boldItalicType = it) }
        }
        if (clazz != PageTextLink::class) result.add(parsedText(Link))
        if (clazz != PageTextTemplate::class) result.add(parsedText(Template))

        return result
    }

}
