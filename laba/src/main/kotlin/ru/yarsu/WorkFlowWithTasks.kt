package ru.yarsu

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.collections.mutableListOf as mutableListOf

class WorkFlowWithTasks(
    private val tasksData: List<TaskModel>,
) {
    fun getSortedTaskList(): List<TasksForListCommand> {
        val sortedFilteredTasks =
            tasksData.sortedWith(
                compareBy<TaskModel> { it.registrationDateTime }.thenBy { it.id },
            )

        val totalSortedFilteredTaskList = mutableListOf<TasksForListCommand>()

        sortedFilteredTasks.forEach(
            { task ->
                totalSortedFilteredTaskList.add(
                    TasksForListCommand(
                        id = task.id,
                        title = task.title,
                        isClosed = task.percentage == 100,
                    ),
                )
            },
        )
        return totalSortedFilteredTaskList
    }

    fun getTaskById(id: UUID): TaskModel {
        val taskById = tasksData.find { it.id == id }
        if (taskById == null) {
            throw NullPointerException("Задача не найдена")
        }
        return taskById
    }

    fun getListEisenHower(
        important: Boolean?,
        urgent: Boolean?,
    ): List<TaskForListImportance> {
        if (important == null && urgent == null) {
            throw IllegalArgumentException("Отсутствуют оба параметра important и urgent")
        }
        val importantStatusTask = listOf(Importance.CRITICAL.importance, Importance.VERY_HIGH.importance, Importance.HIGH.importance)
        val unimportantStatusTask = listOf(Importance.LOW.importance, Importance.VERY_LOW.importance, Importance.DEFAULT.importance)
        val filteredTasks =
            tasksData
                .filter { task ->
                    (
                        important == null ||
                            (important && task.importance in importantStatusTask) ||
                            (!important && task.importance in unimportantStatusTask)
                    ) &&
                        (urgent == null || ((urgent && task.urgency == urgent) || (!urgent && task.urgency == urgent)))
                }.sortedWith(
                    compareBy<TaskModel> { it.registrationDateTime }
                        .thenBy { it.id },
                )

        val taskForListImportance = mutableListOf<TaskForListImportance>()
        filteredTasks.forEach({ task ->
            taskForListImportance.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance,
                    urgency = task.urgency,
                    percentage = task.percentage,
                ),
            )
        })
        return taskForListImportance
    }

    fun getListTime(
        tasksData: List<TaskModel>,
        inputDateTime: LocalDateTime?,
    ): List<TaskForListImportance> {
        val listSorted =
            tasksData
                .filter { task ->
                    (task.startDateTime < inputDateTime) && (task.percentage < 100)
                }.sortedWith(
                    compareByDescending<TaskModel> { it.importance }
                        .thenByDescending { it.urgency }
                        .thenBy { it.registrationDateTime }
                        .thenBy { it.id },
                )

        val taskList = mutableListOf<TaskForListImportance>()
        listSorted.forEach({ task ->
            taskList.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance,
                    urgency = task.urgency,
                    percentage = task.percentage,
                ),
            )
        })
        return taskList
    }

    //
//
    fun getStatisticDate(typeStatistic: ValuesStatistic): Map<String, Int> {
        val dayCount: MutableMap<String, Int> = mutableMapOf()

        val dayOfWeekTranslations =
            mapOf(
                "MONDAY" to "Понедельник",
                "TUESDAY" to "Вторник",
                "WEDNESDAY" to "Среда",
                "THURSDAY" to "Четверг",
                "FRIDAY" to "Пятница",
                "SATURDAY" to "Суббота",
                "SUNDAY" to "Воскресенье",
            )

        val weekDaysOrder =
            listOf(
                "Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье",
                "Не заполнено",
            )

        for (task in tasksData) {
            val dateString =
                when (typeStatistic) {
                    ValuesStatistic.REGISTRATION -> task.registrationDateTime
                    ValuesStatistic.START -> task.startDateTime
                    ValuesStatistic.END -> task.endDateTime ?: ""
                }

            if (dateString.toString().isNotEmpty()) {
                val date = LocalDateTime.parse(dateString.toString(), DateTimeFormatter.ISO_DATE_TIME)
                val dayOfWeek = dayOfWeekTranslations[date.dayOfWeek.name] ?: date.dayOfWeek.name
                dayCount[dayOfWeek] = dayCount.getOrDefault(dayOfWeek, 0) + 1
            } else if (typeStatistic == ValuesStatistic.END) {
                dayCount["Не заполнено"] = dayCount.getOrDefault("Не заполнено", 0) + 1
            }
        }

        return dayCount
    }
}
