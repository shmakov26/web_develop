package ru.yarsu

import com.beust.jcommander.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import java.util.*
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.*
import kotlin.system.exitProcess

fun main(argv: Array<String>) {
    val taskList = TaskList()
    val showTask = ShowTask()
    val listEisenHower = ListEisenHower()
    val listTime = ListTime()
    val statistic = Statistic()

    val commander: JCommander = JCommander
        .newBuilder()
        .addCommand("list", taskList)
        .addCommand("show", showTask)
        .addCommand("list-importance", listEisenHower)
        .addCommand("list-time", listTime)
        .addCommand("statistic", statistic)
        .build()

    val dataForView: Any
    try {
        val data: List<List<String>>
        commander.parse(*argv)
        val csvReader = CsvReader()
        data = when{
            taskList.urlFile != "" -> csvReader.readAll(File(taskList.urlFile))
            showTask.urlFile != "" -> csvReader.readAll(File(showTask.urlFile))
            listEisenHower.urlFile != "" -> csvReader.readAll(File(listEisenHower.urlFile))
            listTime.urlFile != "" -> csvReader.readAll(File(listTime.urlFile))
            statistic.urlFile != "" -> csvReader.readAll(File(statistic.urlFile))
            else -> {
                throw IllegalArgumentException("Пропущен аргумент")
            }
        }

        val dataTask = mutableListOf<TaskModel>()
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")
        for (item in data.drop(1)) {
            dataTask.add(
                TaskModel(
                    UUID.fromString(item[0]),
                    title = item[1],
                    registrationDateTime = LocalDateTime.parse(item[2], format),
                    startDateTime = LocalDateTime.parse(item[3], format),
                    endDateTime = item[4].takeIf { it.isNotBlank() }?.let { LocalDateTime.parse(it, format) },
                    importance = parseImportance(item[5]),
                    urgency = item[6].toBoolean(),
                    percentage = item[7].toInt(),
                    description = item[8]
                )
            )
        }
        val workFlowWithTasks = WorkFlowWithTasks(dataTask)
        dataForView = when (commander.parsedCommand) {
            "list" -> workFlowWithTasks.getSortedTaskList()
            "show" -> {
                workFlowWithTasks.getTaskById(UUID.fromString(showTask.taskID))
            }

            "list-importance" -> {
                workFlowWithTasks.getListEisenHower(listEisenHower.important, listEisenHower.urgent)
            }

            "list-time" -> {
                val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.S"
                val format = DateTimeFormatter.ofPattern(dateFormat)
                if(listTime.time == null){
                    throw NullPointerException("Дата не может быть нулевой")
                }
                workFlowWithTasks.getSortedListByManyParametresTask(dataTask, LocalDateTime.parse(listTime.time, format))

            }

            "statistic" -> {
                val valueStatic: String = statistic.valueStatistic
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
    }
    catch (e: Exception){
        System.err.println("Ошибка! Приложение использовано некорретно. Подробности ошибки: $e")
        commander.usage()
        exitProcess(1)
    }

    //Вывод для высокоуровневого интерфейса
    val mapper = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .defaultPropertyInclusion(JsonInclude.Value.construct(
            JsonInclude.Include.NON_NULL,
            JsonInclude.Include.ALWAYS
        ))
        .build()
    val printer = DefaultPrettyPrinter()
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
    mapper.writeValue(System.out, dataForView)
}
