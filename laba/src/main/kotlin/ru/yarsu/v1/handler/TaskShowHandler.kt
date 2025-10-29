package ru.yarsu.v1.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.Category
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithCategory
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.v1.serializers.BaseSerializer
import ru.yarsu.v1.serializers.TaskShowSerializer
import java.util.UUID

class TaskShowHandler(
    private val tasklist: List<TaskModel>,
    private val categoryList: List<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val taskId: String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)

        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val workFlowWithCategory = WorkFlowWithCategory(categoryList)

        val taskShowSerializer = TaskShowSerializer()

        try {
            val uuid = UUID.fromString(taskId)
            val task = workFlowWithTasks.getTaskById(uuid)
            val category = workFlowWithCategory.getCategoryByUUID(task.category)
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeeTask(task, category?.description))
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

class TaskHandler : HttpHandler {
    override fun invoke(request: Request): Response {
        val baseSerializer = BaseSerializer()
        return Response(Status.BAD_REQUEST)
            .contentType(ContentType.APPLICATION_JSON)
            .body(baseSerializer.serializeError("Отсутствует обязательный параметр task-id"))
    }
}
