package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class Category(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Description")
    val description: String,
    @JsonProperty("Color")
    val color: Color,
)

data class TaskModel(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("RegistrationDateTime")
    val registrationDateTime: LocalDateTime,
    @JsonProperty("StartDateTime")
    val startDateTime: LocalDateTime,
    @JsonProperty("EndDateTime")
    val endDateTime: LocalDateTime?,
    @JsonProperty("Importance")
    var importance: Importance,
    @JsonProperty("Urgency")
    val urgency: Boolean,
    @JsonProperty("Percentage")
    val percentage: Int,
    @JsonProperty("Description")
    val description: String,
    @JsonProperty("IsClosed")
    val isClosed: Boolean,
    @JsonProperty("Category")
    val category: UUID,
)

// view's for list command
data class TasksForListCommand(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("IsClosed")
    val isClosed: Boolean,
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

// view's for list-time
data class TaskForListTime(
    @JsonProperty("time")
    val time: String,
    @JsonProperty("tasks")
    val tasks: List<TaskForListImportance>,
)
