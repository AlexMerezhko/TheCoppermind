package com.thecoppermind.page

import com.google.gson.*
import com.thecoppermind.utils.getIdForLink
import java.lang.reflect.Type

data class PageData(val id: Int, val title: String, val parts: ArrayList<PageTextInterface>)

interface PageTextInterface {
    val text: String
}

data class PageTextNormal(override val text: String) : PageTextInterface
data class PageTextHeading(override val text: String) : PageTextInterface
data class PageTextBold(override val text: String) : PageTextInterface
data class PageTextLink(override val text: String, val pageId: String) : PageTextInterface
data class PageTextTemplate(override val text: String) : PageTextInterface

class PageClassDeserializer : JsonDeserializer<PageData> {

    // ----- Десериализатор -----

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PageData {
        val rootObject = json.asJsonObject.get("parse").asJsonObject
        return getPageData(rootObject)
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
//        val notNormalTextVariants = listOf(ContentType.Bold, ContentType.Link, ContentType.Heading, ContentType.Template)

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
                        index = parseAndAddText(parts, charArrayToParse, index, count)
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

            if (rawLink.contains("|")) {
                linkId = rawLink.substringBefore("|").getIdForLink()
                if (postfixSize > 0) {
                    linkText = rawLink.substringAfterLast("|") + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                } else {
                    linkText = rawLink.substringAfterLast("|")
                }
            } else {
                linkId = rawLink.getIdForLink()
                if (postfixSize > 0) {
                    linkText = rawLink + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                } else {
                    linkText = rawLink.substringAfterLast("|")
                }
            }

            if (rawLink.isNotEmpty()) {
                parts.add(PageTextLink(linkText, linkId))
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

        // TODO { tempaltes and h3 h2 h1
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
            val endPosition = charArrayToParse.getContentLength(ContentType.Heading, startPosition + ContentType.Heading.charsCountInRow)
            val text = parseText(charArrayToParse, startPosition + ContentType.Heading.charsCountInRow, endPosition)
            if (text.trim().isNotEmpty()) {
                parts.add(PageTextHeading(text.trim()))
            }
            return endPosition + ContentType.Heading.charsCountInRow
        }

        // ----- Обычный текст -----

        fun parseAndAddText(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int, count: Int): Int {

            // вырезаем все переносы строк в начале части текста
            var startAfterNewLine = startPosition
            while (startAfterNewLine < count && charArrayToParse[startAfterNewLine] == '\n') {
                startAfterNewLine++
            }

            val endPosition = charArrayToParse.getLengthBeforeContents(notNormalTextVariants, startPosition)

            // вырезаем все переносы строк в конце части текста
            var endBeforeNewLine = endPosition
            while (endBeforeNewLine > startAfterNewLine && charArrayToParse[endBeforeNewLine - 1] == '\n') {
                endBeforeNewLine--
            }

            val text = parseText(charArrayToParse, startAfterNewLine, endBeforeNewLine)
            if (text.isNotEmpty()) {
                // форматирование перечислений (возможно потребуется доработка)
                parts.add(PageTextNormal(text.replace(Regex("[*#]"), " - ")))
            }
            return endPosition
        }
    }
}

