package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.time.LocalDateTime
import java.util.UUID

// Базовый вид
data class TaskModel(
    @field:JsonProperty("ID")
    val id: UUID,
    @field:JsonProperty("Title")
    val title: String,
    @field:JsonProperty("RegistrationDateTime")
    val registrationDateTime: LocalDateTime,
    @field:JsonProperty("StartDateTime")
    val startDateTime: LocalDateTime,
    @field:JsonProperty("EndDateTime")
    val endDateTime: LocalDateTime?,
    @field:JsonProperty("Importance")
    val importance: Importance,
    @field:JsonProperty("Urgency")
    val urgency: Boolean,
    @field:JsonProperty("Percentage")
    val percentage: Int,
    @field:JsonProperty("Description")
    val description: String
)
// Другие виды

// Просмотр команды list
data class TasksForListCommand(
    @field:JsonProperty("ID")
    val id: UUID,
    @field:JsonProperty("Title")
    val title: String,
    @field:JsonProperty("IsClosed")
    val isClosed: Boolean,
)

data class TaskCommandList(
    @field:JsonProperty("tasks")
    val tasks: List<TasksForListCommand>,
)

// Посмотреть команду show
data class ParticularTask(
    @field:JsonProperty("task-id")
    val id: UUID,
    @field:JsonProperty("task")
    val task: TaskModelShow,
)

data class TaskModelShow(
    @field:JsonProperty("ID")
    val id: UUID,
    @field:JsonProperty("Title")
    val title: String,
    @field:JsonProperty("RegistrationDateTime")
    val registrationDateTime: String,
    @field:JsonProperty("StartDateTime")
    val startDateTime: String,
    @field:JsonProperty("EndDateTime")
    val endDateTime: String?,
    @field:JsonProperty("Importance")
    val importance: String,
    @field:JsonProperty("Urgency")
    val urgency: Boolean,
    @field:JsonProperty("Percentage")
    val percentage: Int,
    @field:JsonProperty("Description")
    val description: String,
)

// Просмотр для List-eisenhower
data class ListImportance(
    @field:JsonProperty("important")
    val important: Boolean?,
    @field:JsonProperty("urgent")
    val urgent: Boolean?,
    @field:JsonProperty("tasks")
    val tasks: List<TaskForListImportance>,
)

data class TaskForListImportance(
    @field:JsonProperty("Id")
    val id: UUID,
    @field:JsonProperty("Title")
    val title: String,
    @field:JsonProperty("Importance")
    val importance: String,
    @field:JsonProperty("Urgency")
    val urgency: Boolean,
    @field:JsonProperty("Percentage")
    val percentage: Int,
)

// Просмотр для list-time
data class TaskForListTime(
    @field:JsonProperty("time")
    val time: String,
    @field:JsonProperty("tasks")
    val tasks: List<TaskForListImportance>,
)
