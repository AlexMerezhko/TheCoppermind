package com.thecoppermind.utils

/**
 * Получаем идентификатор страницы по его заголовку (заменяя пробелы на подчёркивания)
 */
fun String.getIdForLink(): String {
    return trim().replace(" ", "_")
}

/**
 * Преобразование символов перечислений в другой вид
 */
fun String.getTextWithEnumeration(): String {
    return replace(Regex("[*#]"), " - ")
}
