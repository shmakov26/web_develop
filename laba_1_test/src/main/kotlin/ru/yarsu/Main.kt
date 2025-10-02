package ru.yarsu

import ru.yarsu.cli.ArgumentParser
import ru.yarsu.cli.Command
import ru.yarsu.csv.CsvReader
import ru.yarsu.json.JsonConverter
import ru.yarsu.service.TaskService

fun main(args: Array<String>) {
    try {
        when (val command = ArgumentParser().parse(args)) {
            is Command.ListCommand -> executeListCommand(command)
            is Command.ShowCommand -> executeShowCommand(command)
            is Command.ListEisenhowerCommand -> executeListEisenhowerCommand(command)
            is Command.ListTimeCommand -> executeListTimeCommand(command)
            is Command.StatisticCommand -> executeStatisticCommand(command)
            is Command.InvalidCommand -> {
                System.err.println("Ошибка: Неверно переданы аргументы.")
                System.exit(1)
            }
        }
    } catch (e: Exception) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    }
}

private fun executeListCommand(command: Command.ListCommand) {
    try {
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        val taskService = TaskService()
        val taskListItems = taskService.processTasksForListCommand(tasks)

        val jsonOutput = JsonConverter.convertToTaskListJson(taskListItems)
        println(jsonOutput)

    } catch (e: IllegalArgumentException) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Ошибка: Не удалось обработать задачи")
        System.exit(1)
    }
}

private fun executeShowCommand(command: Command.ShowCommand) {
    try {
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        val taskService = TaskService()
        val task = taskService.findTaskById(tasks, command.taskId)

        val jsonOutput = JsonConverter.convertToTaskShowJson(command.taskId, task)
        println(jsonOutput)

    } catch (e: IllegalArgumentException) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Ошибка: Не удалось обработать задачу")
        System.exit(1)
    }
}

private fun executeListEisenhowerCommand(command: Command.ListEisenhowerCommand) {
    try {
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        val taskService = TaskService()
        val eisenhowerTasks = taskService.filterTasksForEisenhower(
            tasks,
            command.important,
            command.urgent
        )

        val jsonOutput = JsonConverter.convertToEisenhowerJson(
            command.important,
            command.urgent,
            eisenhowerTasks
        )
        println(jsonOutput)
    } catch (e: IllegalArgumentException) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Ошибка: Не удалось обработать задачи Эйзенхауэра")
    }
}

private fun executeListTimeCommand(command: Command.ListTimeCommand) {
    try {
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        val taskService = TaskService()
        val timeTasks = taskService.filterTasksByTime(tasks, command.time)

        val jsonOutput = JsonConverter.convertToTimeJson(
            command.time.toString(),
            timeTasks
        )
        println(jsonOutput)

    } catch (e: IllegalArgumentException) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Ошибка: не удалось обработать задачи на основе времени")
        System.exit(1)
    }
}

private fun executeStatisticCommand(command: Command.StatisticCommand) {
    try {
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        val taskService = TaskService()
        val statistics = taskService.calculateStatisticsByDateType(tasks, command.dataType)

        val jsonOutput = JsonConverter.convertToStatisticJson(command.dataType, statistics)
        println(jsonOutput)

    } catch (e: IllegalArgumentException) {
        System.err.println("Ошибка: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Ошибка: не удалось вычислять статистику")
        System.exit(1)
    }
}
