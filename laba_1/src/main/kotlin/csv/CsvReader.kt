package org.example.csv

import org.example.data.Task
import org.example.data.TaskImportance
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

class CsvReader {
    fun readTasksFromFile(filePath: String): List<Task> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filePath")
        }

        return file.readLines()
            .drop(1) // Skip header
            .map { line -> parseTaskLine(line) }
    }

    private fun parseTaskLine(line: String): Task {
        val parts = line.split(",").map { it.trim() }

        return Task(
            Id = UUID.fromString(parts[0]),
            Title = parts[1],
            RegistrationDateTime = LocalDateTime.parse(parts[2]),
            StartDateTime = LocalDateTime.parse(parts[3]),
            EndDateTime = parts[4].takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it) },
            Importance = TaskImportance.valueOf(parts[5].uppercase().replace(" ", "_")),
            Urgency = parts[6].toBoolean(),
            Percentage = parts[7].toInt(),
            Description = parts[8]
        )
    }
}