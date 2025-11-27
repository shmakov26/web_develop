package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class User(
    @field:JsonProperty("Id")
    val id: UUID,
    @field:JsonProperty("Login")
    val login: String,
    @field:JsonProperty("RegistrationDateTime")
    val registrationDateTime: String,
    @field:JsonProperty("Email")
    val email: String,
)

data class Category(
    @field:JsonProperty("Id")
    val id: UUID,
    @field:JsonProperty("Description")
    val description: String,
    @field:JsonProperty("Color")
    val color: Color,
    @field:JsonProperty("Owner")
    val owner: UUID?,
)

data class CategoryList(
    @field:JsonProperty("Id")
    val id: UUID,
    @field:JsonProperty("Description")
    val description: String,
    @field:JsonProperty("Color")
    val color: Color,
    @field:JsonProperty("Owner")
    val owner: UUID?,
    @field:JsonProperty("OwnerName")
    val ownerName: String?,
)

data class TaskModel(
    @field:JsonProperty("Id")
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
    var importance: String,
    @field:JsonProperty("Urgency")
    val urgency: Boolean,
    @field:JsonProperty("Percentage")
    val percentage: Int,
    @field:JsonProperty("Description")
    val description: String,
    @field:JsonProperty("IsClosed")
    val isClosed: Boolean,
    @field:JsonProperty("Author")
    var author: UUID,
    @field:JsonProperty("Category")
    val category: UUID,
)

// view's for list command
data class TasksForListCommand(
    @field:JsonProperty("Id")
    val id: UUID,
    @field:JsonProperty("Title")
    val title: String,
    @field:JsonProperty("IsClosed")
    val isClosed: Boolean,
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
