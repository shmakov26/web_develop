package org.example.json

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TaskListItem(
    val Id: String,
    val Title: String,
    val IsClosed: Boolean
)

@Serializable
data class TaskListResponse(
    val tasks: List<TaskListItem>
)

object JsonConverter {
    private val json = Json { prettyPrint = true }

    fun convertToTaskListJson(tasks: List<TaskListItem>): String {
        return json.encodeToString(TaskListResponse(tasks))
    }
}