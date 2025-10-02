package org.example.cli

import org.example.data.StatisticDateType
import java.time.LocalDateTime

sealed class Command {
    data class ListCommand(val tasksFilePath: String) : Command()
    data class ShowCommand(val tasksFilePath: String, val taskId: String): Command()
    data class ListEisenhowerCommand(val tasksFilePath: String, val important: Boolean?, val urgent: Boolean?): Command()
    data class ListTimeCommand(var tasksFilePath: String, val time: LocalDateTime): Command()
    data class StatisticCommand(var tasksFilePath: String, val dataType: StatisticDateType): Command()
    object InvalidCommand : Command()
}