package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

//Базовый вид
data class TaskModel(
    @JsonProperty("ID")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("RegistrationDateTime")
    val registrationDateTime: String,

    @JsonProperty("StartDateTime")
    val startDateTime: String,

    @JsonProperty("EndDateTime")
    val endDateTime: String?,

    @JsonProperty("Importance")
    val importance: Importance,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

    @JsonProperty("Description")
    val description: String
)
//Другие виды

// Просмотр команды list
data class TasksForListCommand(
    @JsonProperty("ID")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("IsClosed")
    val isClosed: Boolean

)

data class TaskCommandList(
    @JsonProperty("tasks")
    val tasks: List<TasksForListCommand>
)

// Посмотреть команду show
data class ParticularTask(
    @JsonProperty("task-id")
    val id: UUID,

    @JsonProperty("task")
    val task: TaskModel
)

// Просмотр для List-eisenhower
data class ListImportance(
    @JsonProperty("important")
    val important: Boolean?,

    @JsonProperty("urgent")
    val urgent: Boolean?,

    @JsonProperty("tasks")
    val tasks: List<TaskForListImportance>
)

data class TaskForListImportance(
    @JsonProperty("Id")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("Importance")
    val importance: String,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

    )
// Просмотр для list-time
data class TaskForListTime(
    @JsonProperty("time")
    val time: String,

    @JsonProperty("tasks")
    val tasks: List<TaskForListImportance>

)

