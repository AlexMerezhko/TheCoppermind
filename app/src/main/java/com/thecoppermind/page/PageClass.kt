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

data class PageTextPlain(override val text: String) : PageTextInterface

// TODO https://en.wikipedia.org/wiki/Template:Fake_heading


//    как насчёт ссылки к хеадингу без смены id?

// TODO есть ещё и префиксы ! a[[b]] gives ab.
// TODO есть ещё курсивная ссылка! ''[[a]]''b gives ab.

// TODO
// To link to a section or subsection in the same page, you can use:
// [[#Section name|displayed text]]/


data class PageTextHeading(override val text: String, val parts: List<PageTextInterface> = ArrayList(), val level: HeadingLevel = HeadingLevel.H2) : PageTextInterface

data class PageTextBoldItalic(override val text: String, val type: BoldItalicType) : PageTextInterface
data class PageTextLink(override val text: String, val pageId: String, val heading: String = "") : PageTextInterface
data class PageTextTemplate(override val text: String) : PageTextInterface

enum class HeadingLevel(val countOfBorderChars: Int) {

    // по документации это стиль заголовка страницы и он не должен встречаться в контенте
//    h1(1), // = heading h1 =

    H2(2), // == heading h2 ==
    H3(3), // === heading h3 ===
    H4(4), // ==== heading h4 ====
    H5(5), // ===== heading h5 =====
    H6(6); // ====== heading h6 ======

    companion object {
        fun getLevelFromBorderCharsCount(count: Int) = HeadingLevel.values().firstOrNull { it.countOfBorderChars == count } ?: HeadingLevel.H6
    }
}

enum class BoldItalicType(val countOfBorderChars: Int) {

    Italic(2), // ''italic''
    Bold(3), // '''bold'''
    BoldItalic(5); // '''''bold italics'''''

    companion object {
        fun getTypeFromBorderCharsCount(count: Int) = BoldItalicType.values().first { it.countOfBorderChars == count }
    }
}


class PageClassDeserializer : JsonDeserializer<PageData> {

    // ----- Десериализатор -----

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PageData {
        val rootObject = json.asJsonObject.get("parse").asJsonObject
        return getPageData(rootObject)
    }

    private fun getPageData(rootObject: JsonObject): PageData {

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
            BoldItalic('\'', '\'', 2),
            Link('[', ']', 2),
            Template('{', '}', 2),
            Heading('=', '=', 2)
        }

        val notPlainTextVariants = ContentType.values()

        fun getPageContent(charArrayToParse: CharArray): ArrayList<PageTextInterface> {

            val count: Int = charArrayToParse.size
            var index = 0

            val parts: ArrayList<PageTextInterface> = ArrayList()

            while (index < count) {

                when {
                    charArrayToParse.isStartOfContent(ContentType.BoldItalic, index) -> {
                        index = parseAndAddBoldItalicText(parts, charArrayToParse, index)
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

        private fun CharArray.isStartOfContent(contentType: ContentType, index: Int) = this.isBorderOfContent(index, contentType.charsCountInRow, contentType.startChar)
        private fun CharArray.isEndOfContent(contentType: ContentType, index: Int) = this.isBorderOfContent(index, contentType.charsCountInRow, contentType.endChar)
        private fun CharArray.isBorderOfContent(index: Int, charsCountInRow: Int, borderCharToCompare: Char): Boolean {
            var checkedCharsCount = 0
            do {
                if (index >= size - checkedCharsCount || this[index + checkedCharsCount] != borderCharToCompare) {
                    return false
                }
                checkedCharsCount++
            } while (checkedCharsCount < charsCountInRow)
            return true
        }

        private fun CharArray.getContentLength(contentType: ContentType, startPosition: Int): Int {
            var endPosition = startPosition
            while (!isEndOfContent(contentType, endPosition)) {
                endPosition++
            }
            return endPosition
        }


        private fun CharArray.getContentLengthWithAllDeepsContent(contentType: ContentType, startPosition: Int): Int {
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

        private fun CharArray.getLengthBeforeContents(contentTypes: Array<ContentType>, startPosition: Int): Int {
            var endPosition = startPosition
            // если концом страницы будет текст (а не любой из возможных "контентов", то этот цикл не завершится без условия endPosition < size )
            while (endPosition < size && !contentTypes.any { isStartOfContent(it, endPosition) }) {
                endPosition++
            }
            return endPosition
        }


        private fun parseText(charArrayToParse: CharArray, startPosition: Int, endPosition: Int): String {
            return String(charArrayToParse.copyOfRange(startPosition, endPosition))
        }

        // ----- Жирный текст -----

        private fun parseAndAddBoldItalicText(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            // по умолчанию это курсив, проверяем другие вохможности

            // TODO посчитать количество апострофов и на основе getTypeFromBorderCharsCount выбрать тип, а не этот харкод (
            var type = BoldItalicType.Italic

            if (charArrayToParse.isStartOfContent(ContentType.BoldItalic, startPosition - BoldItalicType.Italic.countOfBorderChars + BoldItalicType.Bold.countOfBorderChars)) {
                type = BoldItalicType.Bold
                if (charArrayToParse.isStartOfContent(ContentType.BoldItalic, startPosition - BoldItalicType.Italic.countOfBorderChars + BoldItalicType.BoldItalic.countOfBorderChars)) {
                    type = BoldItalicType.BoldItalic
                }
            }

            val borderCharsCount = type.countOfBorderChars

            val endPosition = charArrayToParse.getContentLength(ContentType.BoldItalic, startPosition + borderCharsCount)
            val text = parseText(charArrayToParse, startPosition + borderCharsCount, endPosition).trim()

            if (text.isNotEmpty()) {
                // TODO доделать парсинг ссылок, по аналогии с заголовком
                // если в заголовке есть ссылки - сохраняем в заголовке массив элементов из которых состоит этот заголовок
//                val headingItems = getPageContent(text.trim().toCharArray())
//                if (!headingItems.any { it is PageTextLink }) {
//                    headingItems.clear()
//                }
//                parts.add(PageTextHeading(text.trim(), headingItems, HeadingLevel.getLevelFromBorderCharsCount(borderCharsCount)))
                parts.add(PageTextBoldItalic(text, type))
            }

            return endPosition + borderCharsCount
        }

        // ----- Ссылка на другую страинцу -----

        private fun parseAndAddLink(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            val endPosition = charArrayToParse.getContentLength(ContentType.Link, startPosition + ContentType.Link.charsCountInRow)
            val postfixSize = getLinkPostfixSize(charArrayToParse, endPosition + ContentType.Link.charsCountInRow)

            val rawLink = parseText(charArrayToParse, startPosition + ContentType.Link.charsCountInRow, endPosition)

            val linkId: String
            val linkText: String

            if (rawLink.trim().isNotEmpty()) {

                if (rawLink.contains("|")) {
                    val heading: String
                    if (rawLink.contains("#")) {
                        linkId = rawLink.substringBefore("#").getIdForLink()
                        heading = rawLink.substringAfter("#").substringBefore("|")
                    } else {
                        linkId = rawLink.substringBefore("|").getIdForLink()
                        heading = ""
                    }

                    linkText = if (postfixSize > 0) {
                        rawLink.substringAfterLast("|") + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                    } else {
                        rawLink.substringAfterLast("|")
                    }
                    parts.add(PageTextLink(linkText, linkId, heading))
                } else {
                    linkId = rawLink.getIdForLink()
                    linkText = if (postfixSize > 0) {
                        rawLink + parseText(charArrayToParse, endPosition + ContentType.Link.charsCountInRow, endPosition + ContentType.Link.charsCountInRow + postfixSize)
                    } else {
                        rawLink
                    }
                    parts.add(PageTextLink(linkText, linkId))
                }
            }

            return endPosition + ContentType.Link.charsCountInRow + postfixSize
        }

        private fun getLinkPostfixSize(charArrayToParse: CharArray, endPosition: Int): Int {
            var postfixSize = 0
            while (endPosition + postfixSize < charArrayToParse.size
                    && charArrayToParse[endPosition + postfixSize] != ' '
                    && charArrayToParse[endPosition + postfixSize] != '\n'
                    && charArrayToParse[endPosition + postfixSize] != ','
                    && charArrayToParse[endPosition + postfixSize] != '.'
                    && charArrayToParse[endPosition + postfixSize] != '*'
                    && charArrayToParse[endPosition + postfixSize] != '#'
                    && !notPlainTextVariants.any { charArrayToParse.isStartOfContent(it, endPosition + postfixSize) }
                    ) {
                postfixSize++
            }
            return postfixSize
        }

        // ----- Шаблоны -----

        private fun parseAndAddTemplate(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {
            val endPosition = charArrayToParse.getContentLengthWithAllDeepsContent(ContentType.Template, startPosition + ContentType.Template.charsCountInRow)
            val text = parseText(charArrayToParse, startPosition + ContentType.Template.charsCountInRow, endPosition)
            if (text.trim().isNotEmpty()) {
                parts.add(PageTextTemplate(text.trim()))
            }
            return endPosition + ContentType.Template.charsCountInRow
        }

        // ----- Заголовок-----

        private fun parseAndAddHeading(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            var additionalBorderChar = 0
            while (charArrayToParse.isStartOfContent(ContentType.Heading, startPosition + additionalBorderChar + 1)) {
                additionalBorderChar++
            }

            val borderCharsCount = ContentType.Heading.charsCountInRow + additionalBorderChar

            val endPosition = charArrayToParse.getContentLength(ContentType.Heading, startPosition + borderCharsCount)
            val text = parseText(charArrayToParse, startPosition + borderCharsCount, endPosition).trim()

            if (text.isNotEmpty()) {
                // если в заголовке есть ссылки - сохраняем в заголовке массив элементов из которых состоит этот заголовок
                val headingItems = getPageContent(text.toCharArray())
                if (!headingItems.any { it is PageTextLink }) {
                    headingItems.clear()
                }
                parts.add(PageTextHeading(text, headingItems, HeadingLevel.getLevelFromBorderCharsCount(borderCharsCount)))
            }

            // вырезаем все переносы строк после заголовка
            var endAfterNewLine = endPosition + borderCharsCount

            while (endAfterNewLine < charArrayToParse.size && charArrayToParse[endAfterNewLine] == '\n') {
                endAfterNewLine++
            }

            return endAfterNewLine
        }

        // ----- Обычный текст -----

        private fun parseAndAddText(parts: ArrayList<PageTextInterface>, charArrayToParse: CharArray, startPosition: Int): Int {

            val endPosition = charArrayToParse.getLengthBeforeContents(notPlainTextVariants, startPosition)

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
                parts.add(PageTextPlain(text.getTextWithEnumeration()))
            }
            return endPosition
        }
    }
}
