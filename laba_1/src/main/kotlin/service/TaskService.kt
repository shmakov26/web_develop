package org.example.service

import org.example.data.Task
import org.example.json.TaskListItem

class TaskService {
    fun processTasksForListCommand(tasks: List<Task>): List<TaskListItem> {
        return tasks
            .sortedWith(compareBy({ it.RegistrationDateTime }, { it.Id }))
            .map { task ->
                TaskListItem(
                    Id = task.Id.toString(),
                    Title = task.Title,
                    IsClosed = task.IsClosed
                )
            }
    }
}