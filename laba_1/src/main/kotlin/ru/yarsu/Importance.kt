package ru.yarsu

enum class Importance(
    var importance: String,
    val order: Int,
) {
    VERY_LOW("очень низкий", 0),
    LOW("низкий", 1),
    DEFAULT("обычный", 2),
    HIGH("высокий", 3),
    VERY_HIGH("очень высокий", 4),
    CRITICAL("критический", 5),
}

fun parseImportance(importanceString: String): Importance =
    when (importanceString.lowercase()) {
        "очень низкий" -> Importance.VERY_LOW
        "низкий" -> Importance.LOW
        "обычный" -> Importance.DEFAULT
        "высокий" -> Importance.HIGH
        "очень высокий" -> Importance.VERY_HIGH
        "критический" -> Importance.CRITICAL
        else -> throw IllegalArgumentException("Unknown importance: $importanceString")
    }
