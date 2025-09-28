package org.example.json

import org.example.data.Task
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

@Serializable
data class TaskShowItem(
    val Id: String,
    val Title: String,
    val RegistrationDateTime: String,
    val StartDateTime: String,
    val EndDateTime: String?,
    val Importance: String,
    val Urgency: Boolean,
    val Percentage: Int,
    val Description: String,
    val IsClosed: Boolean
)

@Serializable
data class TaskShowResponse(
    val taskId: String,
    val task: TaskShowItem
)

object JsonConverter {
    private val json = Json {
        prettyPrint = true
    }

    fun convertToTaskListJson(tasks: List<TaskListItem>): String {
        return json.encodeToString(TaskListResponse(tasks))
    }

    fun convertToTaskShowJson(taskId: String, task: Task): String {
        val taskResponse = TaskShowItem(
            Id = task.id.toString(),
            Title = task.title,
            RegistrationDateTime = task.registrationDateTime.toString(),
            StartDateTime = task.startDateTime.toString(),
            EndDateTime = task.endDateTime?.toString(),
            Importance = task.importance.strFormat,
            Urgency = task.urgency,
            Percentage = task.percentage,
            Description = task.description,
            IsClosed = task.isClosed
        )

        return json.encodeToString(TaskShowResponse(taskId, taskResponse))
    }
}