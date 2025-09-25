package org.example.cli

sealed class Command {
    data class ListCommand(val tasksFilePath: String) : Command()
    object InvalidCommand : Command()
}