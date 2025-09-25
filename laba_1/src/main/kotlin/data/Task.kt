package org.example.data

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val Id: UUID,
    val Title: String,
    val RegistrationDateTime: LocalDateTime,
    val StartDateTime: LocalDateTime,
    val EndDateTime: LocalDateTime?,
    val Importance: TaskImportance,
    val Urgency: Boolean,
    val Percentage: Int,
    val Description: String
) {
    val IsClosed: Boolean
        get() = Percentage == 100
}