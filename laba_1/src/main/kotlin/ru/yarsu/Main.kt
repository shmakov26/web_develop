package ru.yarsu

import com.beust.jcommander.JCommander
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.system.exitProcess

val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")

fun main(argv: Array<String>) {
    val taskList = TaskList()
    val showTask = ShowTask()
    val listEisenHower = ListEisenHower()
    val listTime = ListTime()
    val statistic = Statistic()

    val commander: JCommander =
        JCommander
            .newBuilder()
            .addCommand("list", taskList)
            .addCommand("show", showTask)
            .addCommand("list-eisenhower", listEisenHower)
            .addCommand("list-time", listTime)
            .addCommand("statistic", statistic)
            .build()

    var dataForView: Any? = null
    try {
        val data: List<List<String>>
        commander.parse(*argv)

        val filePath =
            when (commander.parsedCommand) {
                "list" -> taskList.urlFile
                "show" -> showTask.urlFile
                "list-eisenhower" -> listEisenHower.urlFile
                "list-time" -> listTime.urlFile
                "statistic" -> statistic.urlFile
                else -> throw IllegalArgumentException("Неизвестная команда")
            }

        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("Файл не найден: $filePath")
        }

        if (file.length() != 0L) {
            val csvReader = CsvReader()
            data = csvReader.readAll(file)
            if (data.size > 1) {
                val dataTask = getDataTask(data)
                val workFlowWithTasks = WorkFlowWithTasks(dataTask)
                dataForView =
                    when (commander.parsedCommand) {
                        "list" -> workFlowWithTasks.getSortedTaskList()
                        "show" -> {
                            workFlowWithTasks.getTaskById(UUID.fromString(showTask.taskID))
                        }

                        "list-eisenhower" -> {
                            workFlowWithTasks.getListEisenHower(listEisenHower.important, listEisenHower.urgent)
                        }

                        "list-time" -> {
                            val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.S"
                            val format = DateTimeFormatter.ofPattern(dateFormat)
                            if (listTime.time == null) {
                                throw NullPointerException("Дата не может быть нулевой")
                            }
                            workFlowWithTasks.getSortedListByManyParametresTask(dataTask, LocalDateTime.parse(listTime.time, format))
                        }

                        "statistic" -> {
                            val valueStatic: String =
                                statistic.valueStatistic
                                    ?: throw NullPointerException("ValueStatic нее может быть равным null")
                            workFlowWithTasks.getStatisticDate(parseValuesStatistic(valueStatic))
                            return
                        }

                        else -> {
                            println("Не передана ни одна команда!Документация:")
                            commander.usage()
                            return
                        }
                    }
            } else {
                throw IllegalArgumentException("Файл $file содержит только заголовок")
            }
        } else {
            throw IllegalArgumentException("Файл $file пустой")
        }
    } catch (e: IllegalArgumentException) {
        System.err.println("$e")
        exitProcess(1)
    } catch (e: NullPointerException) {
        System.err.println("$e")
        exitProcess(1)
    } catch (e: Exception) {
        System.err.println("Ошибка! Приложение использовано некорретно. Подробности ошибки: $e")
        commander.usage()
        exitProcess(1)
    }

    // Вывод для высокоуровневого интерфейса
    enterInterface(dataForView)
}

private fun getDataTask(data: List<List<String>>): MutableList<TaskModel> {
    val dataTask = mutableListOf<TaskModel>()
    for (item in data.drop(1)) {
        dataTask.add(
            TaskModel(
                id = UUID.fromString(item[0]),
                title = item[1],
                registrationDateTime = LocalDateTime.parse(item[2], format),
                startDateTime = LocalDateTime.parse(item[3], format),
                endDateTime = item[4].takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it, format) },
                importance = parseImportance(item[5]),
                urgency = item[6].toBoolean(),
                percentage = item[7].toInt(),
                description = item[8],
            ),
        )
    }
    return dataTask
}

private fun enterInterface(dataForView: Any) {
    val mapper =
        JsonMapper
            .builder()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .defaultPropertyInclusion(
                JsonInclude.Value.construct(
                    JsonInclude.Include.NON_NULL,
                    JsonInclude.Include.ALWAYS,
                ),
            ).build()
    val printer = DefaultPrettyPrinter()
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
    mapper.writeValue(System.out, dataForView)
}
