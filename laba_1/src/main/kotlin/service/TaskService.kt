package org.example.service

import org.example.data.Task
import org.example.json.TaskListItem
import java.util.UUID
import org.example.json.EisenhowerTaskItem
import org.example.data.TaskImportance
import org.example.json.TimeTaskItem
import java.time.LocalDateTime

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

    fun filterTasksForEisenhower(tasks: List<Task>, important: Boolean?, urgent: Boolean?): List<EisenhowerTaskItem> {
        return tasks
            .filter { task ->
                val matchesImportant = important?.let { isTaskImportant(task) == it } ?: true
                val matchesUrgent = urgent?.let { task.urgency == it } ?: true

                matchesImportant && matchesUrgent
            }
            .sortedWith(compareBy({ it.registrationDateTime }, { it.id }))
            .map { task ->
                EisenhowerTaskItem(
                    Id = task.id.toString(),
                    Title = task.title,
                    Importance = task.importance.strFormat,
                    Urgency = task.urgency,
                    Percentage = task.percentage
                )
            }
    }

    private fun isTaskImportant(task: Task): Boolean? {
        return when (task.importance) {
            TaskImportance.HIGH,
            TaskImportance.VERY_HIGH,
            TaskImportance.CRITICAL -> true

            TaskImportance.VERY_LOWER,
            TaskImportance.LOWER,
            TaskImportance.ORDINARY -> false

            TaskImportance.DEFAULT -> null
        }
    }

    fun filterTasksByTime(tasks: List<Task>, time: LocalDateTime): List<TimeTaskItem> {
        return tasks
            .filter { task ->
                !task.startDateTime.isAfter(time) && task.percentage < 100
            }
            .sortedWith(compareByDescending<Task> { task ->
                task.importance.intFormat
            }.thenByDescending { task ->
                task.urgency
            }.thenBy { task ->
                task.registrationDateTime
            }.thenBy { task ->
                task.id
            })
            .map { task ->
                TimeTaskItem(
                    Id = task.id.toString(),
                    Title = task.title,
                    Importance = task.importance.strFormat,
                    Urgency = task.urgency,
                    Percentage = task.percentage
                )
            }
    }
}