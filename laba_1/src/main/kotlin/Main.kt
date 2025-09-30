package org.example

import org.example.cli.ArgumentParser
import org.example.cli.Command
import org.example.csv.CsvReader
import org.example.json.JsonConverter
import org.example.service.TaskService

fun main(args: Array<String>) {
    try {
        when (val command = ArgumentParser().parse(args)) {
            is Command.ListCommand -> handleListCommand(command)
            is Command.ShowCommand -> handleShowCommand(command)
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

private fun handleListCommand(command: Command.ListCommand) {
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

private fun handleShowCommand(command: Command.ShowCommand) {
    try {
        // Читаем задачи из CSV
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        // Ищем задачу по ID
        val taskService = TaskService()
        val task = taskService.findTaskById(tasks, command.taskId)

        // Конвертируем в JSON и выводим
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