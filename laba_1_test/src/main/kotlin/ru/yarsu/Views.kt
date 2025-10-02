package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

//Базовый вид
data class TaskModel(
    @param:JsonProperty("ID")
    val id: UUID,

    @param:JsonProperty("Title")
    val title: String,

    @param:JsonProperty("RegistrationDateTime")
    val registrationDateTime: LocalDateTime,

    @param:JsonProperty("StartDateTime")
    val startDateTime: LocalDateTime,

    @param:JsonProperty("EndDateTime")
    val endDateTime: LocalDateTime?,

    @param:JsonProperty("Importance")
    val importance: Importance,

    @param:JsonProperty("Urgency")
    val urgency: Boolean,

    @param:JsonProperty("Percentage")
    val percentage: Int,

    @param:JsonProperty("Description")
    val description: String
)
//Другие виды

// Просмотр команды list
data class TasksForListCommand(
    @param:JsonProperty("ID")
    val id: UUID,

    @param:JsonProperty("Title")
    val title: String,

    @param:JsonProperty("IsClosed")
    val isClosed: Boolean

)

data class TaskCommandList(
    @param:JsonProperty("tasks")
    val tasks: List<TasksForListCommand>
)

// Посмотреть команду show
data class ParticularTask(
    @param:JsonProperty("task-id")
    val id: UUID,

    @param:JsonProperty("task")
    val task: TaskModelShow
)

data class TaskModelShow(
    @param:JsonProperty("ID")
    val id: UUID,

    @param:JsonProperty("Title")
    val title: String,

    @param:JsonProperty("RegistrationDateTime")
    val registrationDateTime: LocalDateTime,

    @param:JsonProperty("StartDateTime")
    val startDateTime: LocalDateTime,

    @param:JsonProperty("EndDateTime")
    val endDateTime: LocalDateTime?,

    @param:JsonProperty("Importance")
    val importance: String,

    @param:JsonProperty("Urgency")
    val urgency: Boolean,

    @param:JsonProperty("Percentage")
    val percentage: Int,

    @param:JsonProperty("Description")
    val description: String
)

// Просмотр для List-eisenhower
data class ListImportance(
    @param:JsonProperty("important")
    val important: Boolean?,

    @param:JsonProperty("urgent")
    val urgent: Boolean?,

    @param:JsonProperty("tasks")
    val tasks: List<TaskForListImportance>
)

data class TaskForListImportance(
    @param:JsonProperty("Id")
    val id: UUID,

    @param:JsonProperty("Title")
    val title: String,

    @param:JsonProperty("Importance")
    val importance: String,

    @param:JsonProperty("Urgency")
    val urgency: Boolean,

    @param:JsonProperty("Percentage")
    val percentage: Int,

    )
// Просмотр для list-time
data class TaskForListTime(
    @param:JsonProperty("time")
    val time: String,

    @param:JsonProperty("tasks")
    val tasks: List<TaskForListImportance>

)

