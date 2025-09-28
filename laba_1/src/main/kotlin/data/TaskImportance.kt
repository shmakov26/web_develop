package org.example.data

enum class TaskImportance(val strFormat: String) {
    VERY_LOWER("очень низкий"),
    LOWER("низкий"),
    ORDINARY("обычный"),
    HIGH("высокий"),
    VERY_HIGH("очень высокий"),
    CRITICAL("критический"),
    DEFAULT("")
}