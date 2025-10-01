package org.example.cli

import java.time.LocalDateTime
import java.time.LocalTime

sealed class Command {
    data class ListCommand(val tasksFilePath: String) : Command()
    data class ShowCommand(val tasksFilePath: String, val taskId: String): Command()
    data class ListEisenhowerCommand(val tasksFilePath: String, val important: Boolean?, val urgent: Boolean?): Command()
    data class ListTimeCommand(var tasksFilePath: String, val time: LocalDateTime): Command()
    object InvalidCommand : Command()
}