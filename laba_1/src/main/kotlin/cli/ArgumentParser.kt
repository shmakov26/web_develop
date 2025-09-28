package org.example.cli

import kotlin.coroutines.Continuation

class ArgumentParser {
    fun parse(args: Array<String>): Command {
        if (args.isEmpty()) {
            return Command.InvalidCommand
        }

        return when (args[0]) {
            "list" -> parseListCommand(args)
            "show" -> parseShowCommand(args)
            else -> Command.InvalidCommand
        }
    }

    private fun parseListCommand(args: Array<String>): Command {
        if (args.size != 2) return Command.InvalidCommand

        if (!args[1].startsWith("--tasks-file=")) return Command.InvalidCommand

        val filePath = args[1].removePrefix("--tasks-file=")
        if (filePath.isBlank()) return Command.InvalidCommand

        return Command.ListCommand(filePath)
    }

    private fun parseShowCommand(args: Array<String>): Command {
        if (args.size != 3) return Command.InvalidCommand

        var tasksFilePath = ""
        var taskId = ""

        for (i in 1 until args.size) {
            when {
                args[i].startsWith("--tasks-file=") -> {
                    tasksFilePath = args[i].removePrefix("--tasks-file=")
                }
                args[i].startsWith("--task-id=") -> {
                    taskId = args[i].removePrefix("--task-id=")
                }
                else -> return Command.InvalidCommand
            }
        }

        if (tasksFilePath.isBlank() || taskId.isBlank()) {
            return Command.InvalidCommand
        }

        return Command.ShowCommand(tasksFilePath, taskId)
    }
}