package ru.yarsu

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.v1.applicationRoutes
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.system.exitProcess

fun main(argv: Array<String>) {
    val args = Args()
    val commander: JCommander =
        JCommander
            .newBuilder()
            .addObject(args)
            .build()
    try {
        val data: List<List<String>>

        commander.parse(*argv)

        val pathToTasksFile = args.urlFile ?: throw ParameterException("Error: missing option --tasks-file")

        val pathToCategoriesFile = args.userFile ?: throw ParameterException("Error: missing option --categories-file")

        // TODO handle uncorrect arg : --tasks-file --users-file --port

        val app = applicationRoutes(readTaskFileCsv(pathToTasksFile), readCategoryFileCsv(pathToCategoriesFile))

        val server = app.asServer(Netty(args.numberPort ?: throw ParameterException("Error: missing option --port"))).start()
    } catch (e: Exception) {
        System.err.println("$e")
        exitProcess(1)
    }
}

fun readTaskFileCsv(pathToTasksFile: String): List<TaskModel> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataTask = mutableListOf<TaskModel>()
    for (item in data.drop(1)) {
        dataTask.add(
            TaskModel(
                UUID.fromString(item[0]),
                title = item[1],
                registrationDateTime = LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME),
                startDateTime = LocalDateTime.parse(item[3]),
                endDateTime = if (item[4] == "") null else LocalDateTime.parse(item[4]),
                importance = parseImportance(item[5]),
                urgency = item[6].toBoolean(),
                percentage = item[7].toInt(),
                description = item[8],
                isClosed = item[7].toInt() == 100,
                category = UUID.fromString(item[9]),
            ),
        )
    }
    return dataTask
}

fun readCategoryFileCsv(pathToTasksFile: String): List<Category> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataOfCategory = mutableListOf<Category>()

    for (item in data.drop(1)) {
        dataOfCategory.add(
            Category(
                id = UUID.fromString(item[0]),
                description = item[1],
                color = Color.valueOf(item[2]),
            ),
        )
    }
    return dataOfCategory
}
//    val mapper = jacksonObjectMapper()
//    val printer = DefaultPrettyPrinter()
//    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
//    mapper.enable(SerializationFeature.INDENT_OUTPUT)
//        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//        .writer(printer)
//        .writeValue(System.out, dataForView)
