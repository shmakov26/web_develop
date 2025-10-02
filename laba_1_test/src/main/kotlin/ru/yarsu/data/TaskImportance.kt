package ru.yarsu.data

enum class TaskImportance(val strFormat: String, val intFormat: Int) {
    VERY_LOWER("очень низкий", 1),
    LOWER("низкий", 2),
    ORDINARY("обычный", 3),
    HIGH("высокий", 4),
    VERY_HIGH("очень высокий", 5),
    CRITICAL("критический", 6),
    DEFAULT("", -1)
}
