package ru.yarsu

enum class Importance(
    var importance: String,
    val order: Int,
) {
    VERY_LOW("Очень низкий", 0),
    LOW("Низкий", 1),
    DEFAULT("Обычный", 2),
    HIGH("Высокий", 3),
    VERY_HIGH("Очень высокий", 4),
    CRITICAL("Критический", 5),
}

fun parseImportance(importanceString: String): Importance =
    when (importanceString) {
        "Очень низкий" -> Importance.VERY_LOW
        "Низкий" -> Importance.LOW
        "Обычный" -> Importance.DEFAULT
        "Высокий" -> Importance.HIGH
        "Очень высокий" -> Importance.VERY_HIGH
        "Критический" -> Importance.CRITICAL
        else -> throw IllegalArgumentException(
            "Неизвестный Importance. Может быть только Очень низкий, Низкий, Обычный, Высокий, Очень высокий, Критический",
        )
    }
