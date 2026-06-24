package ru.yarsu

enum class ValuesStatistic(
    val type: String,
) {
    REGISTRATION("registration"),
    START("start"),
    END("end"),
}

fun parseValuesStatistic(type: String): ValuesStatistic =
    when (type) {
        "registration" -> ValuesStatistic.REGISTRATION
        "start" -> ValuesStatistic.START
        "end" -> ValuesStatistic.END
        else -> throw IllegalArgumentException(
            "Некорректное значение типа статистики. Для параметра by-date ожидается значение типа статистики, но получено $type",
        )
    }
