package com.thecoppermind.page

import com.google.gson.*
import com.thecoppermind.utils.getIdForLink
import com.thecoppermind.utils.getTextWithEnumeration
import java.lang.reflect.Type

data class PageData(val id: Int, val title: String, val parts: ArrayList<PageTextInterface>)

interface PageTextInterface {
    val text: String
}

// TODO FYI https://en.wikipedia.org/wiki/Help:Wiki_markup

data class PageTextNormal(override val text: String) : PageTextInterface
//data class PageTextNormal(val rawText: String) : PageTextInterface {
//    override val text: String
//        get() = rawText.getTextWithEnumeration()
//}

// known templates
// TODO https://en.wikipedia.org/wiki/Template:Fake_heading


data class PageTextHeading(override val text: String, val parts: List<PageTextInterface> = ArrayList(), val level: HeadingLevel = HeadingLevel.h2) : PageTextInterface

data class PageTextBold(override val text: String) : PageTextInterface
data class PageTextLink(override val text: String, val pageId: String, val heading: String = "") : PageTextInterface
data class PageTextTemplate(override val text: String) : PageTextInterface

enum class HeadingLevel(val countOfBorderChars: Int) {

    // по документации это стиль заголовка страницы и он не должен встречаться в контенте
//    h1(1), // = heading h1 =

    h2(2), // == heading h2 ==
    h3(3), // === heading h3 ===
    h4(4), // ==== heading h4 ====
    h5(5), // ===== heading h5 =====
    h6(6); // ====== heading h6 ======

    companion object {
        fun getLevelTypeFromBorderCharsCount(count: Int) = HeadingLevel.values().firstOrNull { it.countOfBorderChars == count } ?: HeadingLevel.h6
    }
}


class PageClassDeserializer : JsonDeserializer<PageData> {

    // ----- Десериализатор -----

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PageData {
        val rootObject = json.asJsonObject.get("parse").asJsonObject
        return getPageData(rootObject)
    }

    fun getPageData(rootObject: JsonObject): PageData {

//            val startTime = System.currentTimeMillis()
        val title = rootObject.get("title").asString
        val pageId = rootObject.get("pageid").asInt

        val charArrayToParse = rootObject.get("wikitext").asJsonObject.get("*").asString.toCharArray()

        val parts: ArrayList<PageTextInterface> = getPageContent(charArrayToParse)

//            val endTime = System.currentTimeMillis()
//            Log.d("Test", "Parse time in millis = " + (endTime - startTime))
        return PageData(pageId, title, parts)
    }

    // ----- Типы разделителей, по которым отличаем обычный текст от спец/сиволов и/или блоков -----
    companion object {
        enum class ContentType(val startChar: Char, val endChar: Char, val charsCountInRow: Int) {
            Bold('\'', '\'', 3),
            Link('[', ']', 2),
            Template('{', '}', 2),
            Heading('=', '=', 2)
        }

        val notNormalTextVariants = ContentType.values()
//        val notNormalTextVariants = listOf(ContentType.Bold, ContentType.Link, ContentType.Template, ContentType.Heading)

        fun getPageContent(charArrayToParse: CharArray): ArrayList<PageTextInterface> {

            val count: Int = charArrayToParse.size
            var index: Int = 0

            var parts: ArrayList<PageTextInterface> = ArrayList()

            while (index < count) {

                when {
                    charArrayToParse.isStartOfContent(ContentType.Bold, index) -> {
                        index = parseAndAddBoldText(parts, charArrayToParse, index)
                    }
                    charArrayToParse.isStartOfContent(ContentType.Link, index) -> {
                        index = parseAndAddLink(parts, charArrayToParse, index)
                    }
                    charArrayToParse.isStartOfContent(ContentType.Template, index) -> {
                        index = parseAndAddTemplate(parts, charArrayToParse, index)
                    }
                    charArrayToParse.isStartOfContent(ContentType.Heading, index) -> {
                        index = parseAndAddHeading(parts, charArrayToParse, index)
                    }
                    else -> {
                        index = parseAndAddText(parts, charArrayToParse, index)
                    }
                }
            }

            return parts
        }

        fun CharArray.isStartOfContent(contentType: ContentType, index: Int) = this.isBorderOfContent(index, contentType.charsCountInRow, contentType.startChar)
        fun CharArray.isEndOfContent(contentType: ContentType, index: Int) = this.isBorderOfContent(index, contentType.charsCountInRow, contentType.endChar)
        fun CharArray.isBorderOfContent(index: Int, charsCountInRow: Int, borderCharToCompare: Char): Boolean {
            var checkedCharsCount = 0
            do {
                if (index >= size - checkedCharsCount || this[index + checkedCharsCount] != borderCharToCompare) {
                    return false
                }
                checkedCharsCount++
            } while (checkedCharsCount < charsCountInRow)
            return true
        }

        fun CharArray.getContentLength(contentType: ContentType, startPosition: Int): Int {
            var endPosition = startPosition
            while (!isEndOfContent(contentType, endPosition)) {
                endPosition++
            }
            return endPosition
        }


        fun CharArray.getContentLengthWithAllDeepsContent(contentType: ContentType, startPosition: Int): Int {
            var deepContentCounter = 1
            var endPosition = startPosition

            while (deepContentCounter > 0) {
                if (isEndOfContent(contentType, endPosition)) {
                    deepContentCounter--
                }
                if (isStartOfContent(contentType, endPosition)) {
                    deepContentCounter++
                }
                if (deepContentCounter > 0) {
                    endPosition++
                }
            }
            return endPosition
        }

        fun CharArray.getLengthBeforeContents(contentTypes: Array<ContentType>, startPosition: Int): Int {
            var endPosition = startPosition
            // если концом страницы будет текст (а не любой из возможных "контентов", то этот цикл не завершится без условия endPosition < size )
            while (endPosition < size && !contentTypes.any { isStartOfContent(it, endPosition) }) {
                endPosition++
            }
            return endPosition
        }


        fun parseText(charArrayToParse: CharArray, startPosition: Int, endPosition: Int): String {
            return String(charArrayToParse.copyOfRange(startPosition, endPosition))
        }

        // ----- Жирный текст -----

        fun parseAndAddBoldText(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {
            val endPosition = charArrayToParse.getContentLength(ContentType.Bold, startPosition + ContentType.Bold.charsCountInRow)
            val text = parseText(charArrayToParse, startPosition + ContentType.Bold.charsCountInRow, endPosition)
            if (text.trim().isNotEmpty()) {
                parts.add(PageTextBold(text.trim()))
            }
            return endPosition + ContentType.Bold.charsCountInRow
        }

        // ----- Ссылка на другую страинцу -----

        fun parseAndAddLink(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            val endPosition = charArrayToParse.getContentLength(ContentType.Link, startPosition + ContentType.Link.charsCountInRow)
            val postfixSize = getLinkPostfixSize(charArrayToParse, endPosition + ContentType.Link.charsCountInRow)

            val rawLink = parseText(charArrayToParse, startPosition + ContentType.Link.charsCountInRow, endPosition);

            val linkId: String
            val linkText: String

            if (rawLink.isNotEmpty()) {
                if (rawLink.contains("|")) {
                    val heading: String
                    if (rawLink.contains("#")) {
                        linkId = rawLink.substringBefore("|").substringBefore("#").getIdForLink()
                        heading = rawLink.substringBefore("|").substringAfter("#")
                    } else {
                        linkId = rawLink.substringBefore("|").getIdForLink()
                        heading = ""
                    }

                    if (postfixSize > 0) {
                        linkText = rawLink.substringAfterLast("|") + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                    } else {
                        linkText = rawLink.substringAfterLast("|")
                    }
                    parts.add(PageTextLink(linkText, linkId, heading))
                } else {
                    linkId = rawLink.getIdForLink()
                    if (postfixSize > 0) {
                        linkText = rawLink + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                    } else {
                        linkText = rawLink.substringAfterLast("|")
                    }
                    parts.add(PageTextLink(linkText, linkId))
                }
            }

            return endPosition + ContentType.Link.charsCountInRow + postfixSize
        }

        fun getLinkPostfixSize(charArrayToParse: CharArray, endPosition: Int): Int {
            var postfixSize = 0
            while (endPosition + postfixSize < charArrayToParse.size
                    && charArrayToParse[endPosition + postfixSize] != ' '
                    && charArrayToParse[endPosition + postfixSize] != '\n'
                    && charArrayToParse[endPosition + postfixSize] != ','
                    && charArrayToParse[endPosition + postfixSize] != '.'
                    && charArrayToParse[endPosition + postfixSize] != '*'
                    && charArrayToParse[endPosition + postfixSize] != '#'
                    && !notNormalTextVariants.any { charArrayToParse.isStartOfContent(it, endPosition + postfixSize) }
                    ) {
                postfixSize++
            }
            return postfixSize
        }

        // ----- Шаблоны -----

        fun parseAndAddTemplate(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {
            val endPosition = charArrayToParse.getContentLengthWithAllDeepsContent(ContentType.Template, startPosition + ContentType.Template.charsCountInRow)
            val text = parseText(charArrayToParse, startPosition + ContentType.Template.charsCountInRow, endPosition)
            if (text.trim().isNotEmpty()) {
                parts.add(PageTextTemplate(text.trim()))
            }
            return endPosition + ContentType.Template.charsCountInRow
        }

        // ----- Заголовок-----

        fun parseAndAddHeading(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            var additionalBorderChar = 0
            while (charArrayToParse.isStartOfContent(ContentType.Heading, startPosition + additionalBorderChar + 1)) {
                additionalBorderChar++
            }

            var borderCharsCount = ContentType.Heading.charsCountInRow + additionalBorderChar

            val endPosition = charArrayToParse.getContentLength(ContentType.Heading, startPosition + borderCharsCount)
            val text = parseText(charArrayToParse, startPosition + borderCharsCount, endPosition)

            if (text.trim().isNotEmpty()) {
                // если в заголовке есть ссылки - сохраняем в заголовке массив элементов из которых состоит этот заголовок
                val headingItems = getPageContent(text.trim().toCharArray())
                if (!headingItems.any { it is PageTextLink }) {
                    headingItems.clear()
                }
                parts.add(PageTextHeading(text.trim(), headingItems, HeadingLevel.getLevelTypeFromBorderCharsCount(borderCharsCount)))
            }

            // вырезаем все переносы строк после заголовка
            var endAfterNewLine = endPosition + borderCharsCount

            while (endAfterNewLine < charArrayToParse.size && charArrayToParse[endAfterNewLine] == '\n') {
                endAfterNewLine++
            }

            return endAfterNewLine
        }

        // ----- Обычный текст -----

        fun parseAndAddText(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            val endPosition = charArrayToParse.getLengthBeforeContents(notNormalTextVariants, startPosition)

            var endBeforeNewLine = endPosition
            if (endPosition < charArrayToParse.size && charArrayToParse.isStartOfContent(ContentType.Heading, endPosition)) {
                // вырезаем все переносы строк в конце части текста, если после неё идёт заголовок
                while (endBeforeNewLine > startPosition && charArrayToParse[endBeforeNewLine - 1] == '\n') {
                    endBeforeNewLine--
                }
            }

            val text = parseText(charArrayToParse, startPosition, endBeforeNewLine)
            if (text.isNotEmpty()) {
                // форматирование перечислений (возможно потребуется доработка)
                parts.add(PageTextNormal(text.getTextWithEnumeration()))
            }
            return endPosition
        }
    }
}
