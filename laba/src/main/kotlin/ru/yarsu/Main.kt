package ru.yarsu

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.http4k.core.then
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.jwt.JwtTools
import ru.yarsu.jwt.UserAssignRoleOperation
import ru.yarsu.v3.applicationRoutes
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
        commander.parse(*argv)

        val pathToTasksFile = args.urlFile ?: throw ParameterException("Error: missing option --tasks-file")
        val pathToCategoriesFile = args.categoriesFile ?: throw ParameterException("Error: missing option --categories-file")
        val pathToUsersFile = args.userFile ?: throw ParameterException("Error: missing option --users-file")
        val secretKey = args.secretKey ?: throw ParameterException("Error: missing option --secret")

        val tasksFile = readTaskFileCsv(pathToTasksFile)
        val categoriesFile = readCategoryFileCsv(pathToCategoriesFile)
        val usersFile = readUserFileCsv(pathToUsersFile)

        val jwtTools = JwtTools(secretKey)
        val userAssignRoleOperation = UserAssignRoleOperation()
        usersFile.forEach { println("JWT for " + it.login + ": Bearer " + jwtTools.createJWTToken(it)) }

        val userLookup: (String) -> User? = { subject ->
            runCatching { UUID.fromString(subject) }
                .getOrNull()
                ?.let { id -> usersFile.find { it.id == id } }
        }

        val app = applicationRoutes(tasksFile, categoriesFile, usersFile)

        val securedApp =
            authenticationFilter(jwtTools, userLookup)
                .then(assignPermissionsFilter(userAssignRoleOperation))
                .then(app)

        Runtime.getRuntime().addShutdownHook(
            object : Thread() {
                override fun run() {
                    super.run()
                    writeTasksToCsv(tasksFile, pathToTasksFile)
                    writeCategoriesToCsv(categoriesFile, pathToCategoriesFile)
                    writeUsersToCsv(usersFile, pathToUsersFile)
                }
            },
        )

        securedApp.asServer(Netty(args.numberPort ?: throw ParameterException("Error: missing option --port"))).start()
    } catch (e: Exception) {
        System.err.println("$e")
        exitProcess(1)
    }
}

fun readTaskFileCsv(pathToTasksFile: String): MutableList<TaskModel> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataTask = mutableListOf<TaskModel>()
    for (item in data.drop(1)) {
        dataTask.add(
            TaskModel(
                id = UUID.fromString(item[0]),
                title = item[1],
                registrationDateTime = LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME),
                startDateTime = LocalDateTime.parse(item[3]),
                endDateTime = if (item[4] == "") null else LocalDateTime.parse(item[4]),
                importance = item[5],
                urgency = item[6].toBoolean(),
                percentage = item[7].toInt(),
                description = item[8],
                isClosed = item[7].toInt() == 100,
                author = UUID.fromString(item[9]),
                category = UUID.fromString(item[10]),
            ),
        )
    }
    return dataTask
}

fun writeTasksToCsv(
    tasks: List<TaskModel>,
    filePath: String,
) {
    csvWriter().open(filePath) {
        writeRow(
            "Id",
            "Title",
            "RegistrationDateTime",
            "StartDateTime",
            "EndDateTime",
            "Importance",
            "Urgency",
            "Percentage",
            "Description",
            "Author",
            "Category",
        )

        tasks.forEach { task ->
            writeRow(
                task.id.toString(),
                task.title,
                task.registrationDateTime.toString(),
                task.startDateTime.toString(),
                task.endDateTime?.toString(),
                task.importance,
                task.urgency,
                task.percentage,
                task.description,
                task.author.toString(),
                task.category.toString(),
            )
        }
    }
}

fun readCategoryFileCsv(pathToTasksFile: String): MutableList<Category> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataOfCategory = mutableListOf<Category>()

    for (item in data.drop(1)) {
        dataOfCategory.add(
            Category(
                id = UUID.fromString(item[0]),
                description = item[1],
                color = Color.valueOf(item[2]),
                owner = if (item[3] == "") null else UUID.fromString(item[3]),
            ),
        )
    }
    return dataOfCategory
}

fun writeCategoriesToCsv(
    categories: List<Category>,
    filePath: String,
) {
    csvWriter().open(filePath) {
        writeRow("Id", "Description", "Color", "Owner")

        categories.forEach { category ->
            writeRow(
                category.id.toString(),
                category.description,
                category.color,
                category.owner?.toString(),
            )
        }
    }
}

fun readUserFileCsv(pathToUserFile: String): MutableList<User> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToUserFile))

    val dataOfUsers = mutableListOf<User>()

    for (item in data.drop(1)) {
        dataOfUsers.add(
            User(
                UUID.fromString(item[0]),
                item[1],
                LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME).toString(),
                item[3],
                parsStrToRole(item[4]),
            ),
        )
    }
    return dataOfUsers
}

fun writeUsersToCsv(
    users: List<User>,
    filePath: String,
) {
    csvWriter().open(filePath) {
        writeRow("Id", "Login", "RegistrationDateTime", "Email", "Role")

        users.forEach { user ->
            writeRow(
                user.id.toString(),
                user.login,
                user.registrationDateTime,
                user.email,
                user.role.altName,
            )
        }
    }
}
