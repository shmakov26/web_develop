package org.example.service

import org.example.data.Task
import org.example.json.TaskListItem
import java.util.UUID

class TaskService {
    fun processTasksForListCommand(tasks: List<Task>): List<TaskListItem> {
        return tasks
            .sortedWith(compareBy({ it.registrationDateTime }, { it.id }))
            .map { task ->
                TaskListItem(
                    Id = task.id.toString(),
                    Title = task.title,
                    IsClosed = task.isClosed
                )
            }
    }

    fun findTaskById(tasks: List<Task>, taskIdString: String): Task {
        val taskId = try {
            UUID.fromString(taskIdString)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Неверный формат ID задачи: $taskIdString")
        }

        return tasks.find { it.id == taskId }
            ?: throw IllegalArgumentException("Задача с ID $taskIdString не найдено")
    }
}