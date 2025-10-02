package ru.yarsu.cli

import ru.yarsu.data.StatisticDateType
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import kotlin.text.removePrefix

class ArgumentParser {
    fun parse(args: Array<String>): Command {
        if (args.isEmpty()) {
            return Command.InvalidCommand
        }

        return when (args[0]) {
            "list" -> parseListCommand(args)
            "show" -> parseShowCommand(args)
            "list-eisenhower" -> parseListEisenhower(args)
            "list-time" -> parseListTime(args)
            "statistic" -> parseStatisticCommand(args)
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

    private fun parseListEisenhower(args: Array<String>): Command {
        if (args.size < 2) return Command.InvalidCommand

        var tasksFilePath = ""
        var important: Boolean? = null
        var urgent: Boolean? = null

        for (i in 1 until args.size) {
            when {
                args[i].startsWith("--tasks-file=") ->
                    tasksFilePath = args[i].removePrefix("--tasks-file=")
                args[i].startsWith("--important=") ->
                    important = parseBoolean(args[i], "--important=")
                args[i].startsWith("--urgent=") ->
                    urgent = parseBoolean(args[i], "--urgent=")
                else -> return Command.InvalidCommand
            }
        }

        if (tasksFilePath.isBlank() || (important == null && urgent == null)) {
            return Command.InvalidCommand
        }

        return Command.ListEisenhowerCommand(tasksFilePath, important, urgent)
    }

    private fun parseBoolean(arg: String, prefix: String): Boolean {
        val value = arg.removePrefix(prefix)
        return when (value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw IllegalArgumentException("Неверное значение boolean: $value")
        }
    }

    private fun parseListTime(args: Array<String>): Command {
        if (args.size != 3) return Command.InvalidCommand

        var tasksFilePath = ""
        var time: LocalDateTime? = null

        for (i in 1 until args.size) {
            when {
                args[i].startsWith("--tasks-file=") -> {
                    tasksFilePath = args[i].removePrefix("--tasks-file=")
                }
                args[i].startsWith("--time=") -> {
                    time = parseTimeArg(args[i])
                }
                else -> return Command.InvalidCommand
            }
        }

        if (tasksFilePath.isBlank() || time == null) {
            return Command.InvalidCommand
        }

        return Command.ListTimeCommand(tasksFilePath, time)
    }

    private fun parseTimeArg(arg: String): LocalDateTime {
        val timeString = arg.removePrefix("--time=")
        return try {
            LocalDateTime.parse(timeString)
        } catch (e: Exception) {
            throw IllegalArgumentException("Неверный формат времени: $timeString")
        }
    }

    private fun parseStatisticCommand(args: Array<String>): Command {
        if (args.size != 3) return Command.InvalidCommand

        var tasksFilePath = ""
        var dataType: StatisticDateType? = null

        for (i in 1 until args.size) {
            when {
                args[i].startsWith("--tasks-file=") -> {
                    tasksFilePath = args[i].removePrefix("--tasks-file=")
                }
                args[i].startsWith("--by-date=") -> {
                    dataType = parseDateTypeArg(args[i])
                }
                else -> return Command.InvalidCommand
            }
        }

        if (tasksFilePath.isBlank() || dataType == null) {
            return Command.InvalidCommand
        }

        return Command.StatisticCommand(tasksFilePath, dataType)
    }

    private fun parseDateTypeArg(arg: String): StatisticDateType {
        val dateTypeString = arg.removePrefix("--by-date=")
        return when (dateTypeString.lowercase()) {
            "registration" -> StatisticDateType.REGISTRATION
            "start" -> StatisticDateType.START
            "end" -> StatisticDateType.END
            else -> throw IllegalArgumentException("Неверный тип даты: $dateTypeString")
        }
    }
}
