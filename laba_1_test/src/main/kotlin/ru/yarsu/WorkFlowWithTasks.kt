package ru.yarsu

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.mutableListOf as mutableListOf

class WorkFlowWithTasks(
    val tasksData: List<TaskModel>
) {
    fun getSortedTaskList() : TaskCommandList
    {
        val sortedFilteredTasks = tasksData.sortedBy {it.registrationDateTime}

        val totalSortedFilteredTaskList = mutableListOf<TasksForListCommand>()

        sortedFilteredTasks.forEach(
            {
            task ->
            totalSortedFilteredTaskList.add(
                TasksForListCommand(
                    id = task.id,
                    title = task.title,
                    isClosed = task.percentage == 100
                )
            )
        })

        val viewTotalSortedFilteredTaskList = TaskCommandList(
            totalSortedFilteredTaskList
        )

        return viewTotalSortedFilteredTaskList
    }
    fun getTaskById(id: UUID) : ParticularTask
    {
        var taskById = tasksData.find { it.id == id }
        if (taskById == null){
            throw NullPointerException("Задание с таким id не найдено!")
        }

        val showByTaskId = TaskModelShow(
            id = taskById.id,
            title = taskById.title,
            registrationDateTime = taskById.registrationDateTime,
            startDateTime = taskById.startDateTime,
            endDateTime = taskById.endDateTime,
            importance = taskById.importance.importance,
            urgency = taskById.urgency,
            percentage = taskById.percentage,
            description = taskById.description
        )

        return ParticularTask(
            id = id,
            task = showByTaskId
        )
    }
    fun getListEisenHower(important: Boolean?, urgent: Boolean?) : ListImportance {
        val filteredTasks = tasksData.filter { task ->
            (important == null || (important && task.importance in listOf(Importance.HIGH, Importance.VERY_HIGH, Importance.CRITICAL)) || (!important && task.importance in listOf(Importance.VERY_LOW, Importance.LOW, Importance.DEFAULT))) && (urgent == null || task.urgency == urgent)
        }
        val taskForListImportance = mutableListOf<TaskForListImportance>()
        filteredTasks.forEach({task ->
            taskForListImportance.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })

        return ListImportance(
            important = important,
            urgent = urgent,
            tasks = taskForListImportance
        )
    }
    fun getSortedListByManyParametresTask(tasksData: List<TaskModel>, inputDateTime: LocalDateTime) : TaskForListTime {
        val listSorted = tasksData.filter { task ->
            task.startDateTime.isBefore(inputDateTime) && task.registrationDateTime.isBefore(inputDateTime) && task.percentage < 100
        }.sortedWith(                                              //sortedWith принимает собственный Comparator
            compareByDescending<TaskModel> { it.importance.order } //убывание важности
                .thenByDescending { it.urgency }
                .thenBy { it.registrationDateTime }
                .thenBy { it.id }
        )

        val taskList = mutableListOf<TaskForListImportance>()
        listSorted.forEach({task ->
            taskList.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })

        return TaskForListTime(
            time = inputDateTime.toString(),
            tasks = taskList
        )

    }


    fun getStatisticDate(typeStatistic: ValuesStatistic) : Unit {
        val dayCount: MutableMap<String, Int> = mutableMapOf()

        val dayOfWeekTranslations = mapOf(
            "MONDAY" to "Понедельник",
            "TUESDAY" to "Вторник",
            "WEDNESDAY" to "Среда",
            "THURSDAY" to "Четверг",
            "FRIDAY" to "Пятница",
            "SATURDAY" to "Суббота",
            "SUNDAY" to "Воскресенье"
        )

        val weekDaysOrder = listOf(
            "Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота", "Воскресенье", "Не заполнено"
        )

        for (task in tasksData) {
            val dateString = when (typeStatistic) {
                ValuesStatistic.REGISTRATION -> task.registrationDateTime
                ValuesStatistic.START -> task.startDateTime
                ValuesStatistic.END -> task.endDateTime
            }

            if (dateString != null) {
                val dayOfWeek = dayOfWeekTranslations[dateString.dayOfWeek.name] ?: dateString.dayOfWeek.name
                dayCount[dayOfWeek] = dayCount.getOrDefault(dayOfWeek, 0) + 1
            } else if (typeStatistic == ValuesStatistic.END) {
                dayCount["Не заполнено"] = dayCount.getOrDefault("Не заполнено", 0) + 1
            }
        }

        val factory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(System.out)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer

        with(outputGenerator) {
            writeStartObject()

            writeFieldName(when(typeStatistic){
                ValuesStatistic.REGISTRATION -> "statisticByRegistrationDateTime"
                ValuesStatistic.START -> "statisticByStartDateTime"
                ValuesStatistic.END -> "statisticByEndDateTime"
            })

            writeStartArray()

            for (day in weekDaysOrder) {
                dayCount[day]?.let { count ->
                    writeStartObject()
                    writeFieldName(day)
                    writeNumber(count)
                    writeEndObject()
                }
            }
            writeEndArray()

            writeEndObject()
            close()
        }
    }
}
