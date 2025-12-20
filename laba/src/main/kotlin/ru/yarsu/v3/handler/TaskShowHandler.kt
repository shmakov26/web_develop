package ru.yarsu.v3.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.LensFailure
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.Category
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.WorkFlowWithUsers
import ru.yarsu.jwt.Permissions
import ru.yarsu.permissionsLens
import ru.yarsu.userContextLens
import ru.yarsu.v3.jsonResponseLens
import ru.yarsu.v3.serializers.TaskShowSerializer
import ru.yarsu.v3.utils.putTask
import ru.yarsu.v3.utils.validateBody
import java.util.UUID

class TaskShowHandler(
    private val tasklist: List<TaskModel>,
    private val userList: List<User>,
    private val categoryList: List<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        userContextLens(request).user ?: return Response(Status.UNAUTHORIZED)

        val taskId: String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)

        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val workFlowWithUsers = WorkFlowWithUsers(userList)

        val taskShowSerializer = TaskShowSerializer()

        try {
            val uuid = UUID.fromString(taskId)
            val task = workFlowWithTasks.getTaskById(uuid)
            val user = workFlowWithUsers.getUserByUUID(task.author)
            val category = categoryList.find { it.id == task.category }?.description ?: "ошибка"

            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeeTask(task, user.email, category))
        } catch (e: NullPointerException) {
            return Response(Status.NOT_FOUND)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeNotFoundTask(taskId, e.message.toString()))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(
                    taskShowSerializer.serializeError(
                        "Некорректный идентификатор задачи. Для параметра task-id ожидается UUID, но получено значение «$taskId»",
                    ),
                )
        }
    }
}

class TaskShowPutHandler(
    private val tasklist: MutableList<TaskModel>,
    private val userList: MutableList<User>,
    private val categoryList: MutableList<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val user = userContextLens(request).user ?: return Response(Status.UNAUTHORIZED)

        val taskId: String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)
        val jsonResponse = jsonResponseLens<Map<String, Any>>()
        try {
            val body = jsonBodyLens(request)
            val listError = validateBody(body, userList, categoryList)

            if (listError.isNotEmpty()) {
                return jsonResponse.invoke(listError, Status.BAD_REQUEST)
            }

            val workFlowWithTasks = WorkFlowWithTasks(tasklist)
            val taskShowSerializer = TaskShowSerializer()

            try {
                val uuid = UUID.fromString(taskId)
                val task = workFlowWithTasks.getTaskById(uuid)
                if (user.id != task.author) {
                    return Response(Status.UNAUTHORIZED)
                }

                val newTask =
                    putTask(
                        body,
                        body["Title"].toString(),
                        UUID.fromString(body["Category"].toString()),
                        task,
                    )

                val author = UUID.fromString(body["Author"].toString())
                val category = UUID.fromString(body["Category"].toString())
                var ownerCategory: UUID? = null

                val index = tasklist.indexOfFirst { it.id == task.id }

                for (categ in categoryList) {
                    if (categ.id == category) {
                        ownerCategory = categ.owner
                    }
                }

                if (ownerCategory == null) {
                    tasklist[index] = newTask
                    return Response(Status.NO_CONTENT)
                } else {
                    if (ownerCategory == author) {
                        tasklist[index] = newTask
                        return Response(Status.NO_CONTENT)
                    } else {
                        return jsonResponse.invoke(
                            mapOf<String, String>(
                                "AuthorId" to author.toString(),
                                "CategoryOwnerId" to ownerCategory.toString(),
                            ),
                            Status.FORBIDDEN,
                        )
                    }
                }
            } catch (e: NullPointerException) {
                return jsonResponse.invoke(mapOf<String, String>("TaskId" to taskId, "Error" to e.message.toString()), Status.NOT_FOUND)
            } catch (e: IllegalArgumentException) {
                return Response(Status.BAD_REQUEST)
                    .contentType(ContentType.APPLICATION_JSON)
                    .body(
                        taskShowSerializer.serializeError(
                            "Некорректный идентификатор задачи. Для параметра task-id ожидается UUID, но получено значение «$taskId»",
                        ),
                    )
            }
        } catch (e: LensFailure) {
            return jsonResponse.invoke(
                mapOf("Value" to request.bodyString(), "Error" to "Missing a name for object member."),
                Status.BAD_REQUEST,
            )
        }
    }
}
