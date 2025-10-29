package ru.yarsu.v1

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.Category
import ru.yarsu.TaskModel
import ru.yarsu.v1.handler.CategoryHandler
import ru.yarsu.v1.handler.EisenhowerListHandler
import ru.yarsu.v1.handler.ListTimeHandler
import ru.yarsu.v1.handler.PingHandler
import ru.yarsu.v1.handler.StatisticHandler
import ru.yarsu.v1.handler.TaskListHandler
import ru.yarsu.v1.handler.TaskShowHandler

fun applicationRoutes(
    taskList: List<TaskModel>,
    categoryList: List<Category>,
): RoutingHttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to PingHandler(),
            "/v1" bind
                routes(
                    "/categories" bind Method.GET to CategoryHandler(categoryList),
                    "/tasks" bind
                        routes(
                            "/" bind Method.GET to TaskListHandler(taskList),
                            "/eisenhower" bind Method.GET to EisenhowerListHandler(taskList),
                            "/by-time" bind Method.GET to ListTimeHandler(taskList),
                            "/statistics" bind Method.GET to StatisticHandler(taskList),
                            "/{task-id}" bind Method.GET to TaskShowHandler(taskList, categoryList),
                        ),
                ),
        )

    return app
}
