package com.thecoppermind.utils


/**
 * Получаем идентификатор страницы по его заголовку (заменяя пробелы на подчёркивания)
 */
fun String.getIdForLink(): String {
    if (contains("|")) {
        return trim().substringBefore("|").replace(" ", "_")
    } else {
        return trim().replace(" ", "_")
    }
}


/**
 * Получаем текст для отображения ссылки на экране
 */
fun String.getTextForLink(): String = substringAfterLast("|")



//if (rawLink.contains("|")) {
//    linkId = rawLink.substringBefore("|").getIdForLink()
//    if (postfixSize > 0) {
//        linkText = rawLink.substringAfterLast("|") + parseText(charArrayToParse, endPosition + PageClassDeserializer.Companion.ContentType.Link.charsCountInRow, endPosition + PageClassDeserializer.Companion.ContentType.Link.charsCountInRow + postfixSize)
//    } else {
//        linkText = rawLink.substringAfterLast("|")
//    }
//} else {
//    linkId = rawLink.getIdForLink()
//    if (postfixSize > 0) {
//        linkText = rawLink + parseText(charArrayToParse, endPosition + PageClassDeserializer.Companion.ContentType.Link.charsCountInRow, endPosition + PageClassDeserializer.Companion.ContentType.Link.charsCountInRow + postfixSize)
//    } else {
//        linkText = rawLink.substringAfterLast("|")
//    }
//}
