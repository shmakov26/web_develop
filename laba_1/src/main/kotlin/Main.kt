package org.example

import org.example.cli.ArgumentParser
import org.example.cli.Command
import org.example.csv.CsvReader
import org.example.json.JsonConverter
import org.example.service.TaskService

fun main(args: Array<String>) {
    try {
        val command = ArgumentParser().parse(args)

        when (command) {
            is Command.ListCommand -> handleListCommand(command)
            is Command.InvalidCommand -> {
                System.err.println("Error: Invalid command arguments. Usage: list --tasks-file=<path>")
                System.exit(1)
            }
        }
    } catch (e: Exception) {
        System.err.println("Error: ${e.message}")
        System.exit(1)
    }
}

private fun handleListCommand(command: Command.ListCommand) {
    try {
        // Read and parse CSV
        val csvReader = CsvReader()
        val tasks = csvReader.readTasksFromFile(command.tasksFilePath)

        // Process tasks
        val taskService = TaskService()
        val taskListItems = taskService.processTasksForListCommand(tasks)

        // Convert to JSON and output
        val jsonOutput = JsonConverter.convertToTaskListJson(taskListItems)
        println(jsonOutput)

    } catch (e: IllegalArgumentException) {
        System.err.println("Error: ${e.message}")
        System.exit(1)
    } catch (e: Exception) {
        System.err.println("Error: Failed to process tasks")
        System.exit(1)
    }
}