package org.example.cli

sealed class Command {
    data class ListCommand(val tasksFilePath: String) : Command()
    data class ShowCommand(val tasksFilePath: String, val taskId: String): Command()
    data class ListEisenhowerCommand(val tasksFilePath: String, val important: Boolean?, val urgent: Boolean?): Command()
    object InvalidCommand : Command()
}