package ru.yarsu.csv

import ru.yarsu.data.Task
import ru.yarsu.data.TaskImportance
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

class CsvReader {
    fun readTasksFromFile(filePath: String): List<Task> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Файл не найден: $filePath")
        }

        return file.readLines()
            .drop(1)
            .map { line -> parseTaskLine(line) }
    }

    private fun replaceOnTaskImportance(importance: String) = when (importance) {
        "очень низкий" -> TaskImportance.VERY_LOWER
        "низкий" -> TaskImportance.LOWER
        "обычный" -> TaskImportance.ORDINARY
        "высокий" -> TaskImportance.HIGH
        "очень высокий" -> TaskImportance.VERY_HIGH
        "критический" -> TaskImportance.CRITICAL
        else -> TaskImportance.DEFAULT
    }

    private fun parseTaskLine(line: String): Task {
        val parts = line.split(",").map { it.trim() }

        return Task(
            id = UUID.fromString(parts[0]),
            title = parts[1],
            registrationDateTime = LocalDateTime.parse(parts[2]),
            startDateTime = LocalDateTime.parse(parts[3]),
            endDateTime = parts[4].takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) },
            importance = replaceOnTaskImportance(parts[5]),
            urgency = parts[6].toBoolean(),
            percentage = parts[7].toInt(),
            description = parts[8]
        )
    }
}
