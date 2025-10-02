package ru.yarsu.data

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID,
    val title: String,
    val registrationDateTime: LocalDateTime,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime?,
    val importance: TaskImportance,
    val urgency: Boolean,
    val percentage: Int,
    val description: String
) {
    val isClosed: Boolean
        get() = percentage == 100
}
