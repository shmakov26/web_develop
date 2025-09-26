package org.example.service

import org.example.data.Task
import org.example.json.TaskListItem

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
}