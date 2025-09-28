package org.example.cli

sealed class Command {
    data class ListCommand(val tasksFilePath: String) : Command()
    data class ShowCommand(val tasksFilePath: String, val taskId: String): Command()
    object InvalidCommand : Command()
}