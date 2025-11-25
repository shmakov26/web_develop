package ru.yarsu.v2

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.Category
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.v1.handler.EisenhowerListHandler
import ru.yarsu.v1.handler.ListTimeHandler
import ru.yarsu.v1.handler.StatisticHandler
import ru.yarsu.v2.handler.AddNewTaskHandler
import ru.yarsu.v2.handler.CategoryHandler
import ru.yarsu.v2.handler.DeleteCategory
import ru.yarsu.v2.handler.PutCategory
import ru.yarsu.v2.handler.PingHandler
import ru.yarsu.v2.handler.TaskListHandler
import ru.yarsu.v2.handler.TaskShowHandler
import ru.yarsu.v2.handler.TaskShowPutHandler
import ru.yarsu.v2.handler.UsersHandler

fun applicationRoutes(
    taskList: MutableList<TaskModel>,
    categoriesList: MutableList<Category>,
    userList: MutableList<User>,
): RoutingHttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to PingHandler(),
            "/v2" bind
                routes(
                    "/tasks" bind Method.GET to TaskListHandler(taskList),
                    "/tasks" bind Method.POST to AddNewTaskHandler(taskList, userList, categoriesList),
                    "/tasks/eisenhower" bind Method.GET to EisenhowerListHandler(taskList),
                    "/tasks/by-time" bind Method.GET to ListTimeHandler(taskList),
                    "/tasks/statistics" bind Method.GET to StatisticHandler(taskList),
                    "/tasks/{task-id}" bind Method.GET to TaskShowHandler(taskList, userList, categoriesList),
                    "/tasks/{task-id}" bind Method.PUT to TaskShowPutHandler(taskList, userList, categoriesList),
                    "/categories" bind Method.GET to CategoryHandler(categoriesList, userList),
                    "/categories/{category-id}" bind Method.PUT to PutCategory(categoriesList),
                    "/categories/{category-id}" bind Method.DELETE to DeleteCategory(taskList, categoriesList),
                    "/users" bind Method.GET to UsersHandler(userList),
                ),
        )
    return app
}

/*
curl -X POST http://localhost:9000/v2/tasks \
-H "Content-Type: application/json" \
-d '{
        "Title": "Лабоработная № 3",
        "RegistrationDateTime": "2024-01-01T00:00:00",
        "StartDateTime": "2024-10-01T00:00:00",
        "EndDateTime": "2025-01-01T00:00:00",
        "Importance": "Очень низкий",
        "Urgency": true,
        "Percentage": 90,
        "Description": "реализовать приложение для лабораторной № 3",
        "Author": "a2c5117e-c202-4e94-a819-6a44e008b301",
        "Category": "10c50d8a-fc9e-45ba-b303-7a623f4c699b"
    }'
*/

/*
curl -X PUT "http://localhost:9000/v2/tasks/8b2a4482-35c8-411e-aab9-6ce490b3bea1" \
     -H "Content-Type: application/json" \
     -d '{
           "Title": "Обновлённая задача",
           "Author": "de42a00f-7f43-4d10-808d-bee47fdeef49",
           "Category": "10c50d8a-fc9e-45ba-b303-7a623f4c699b"
         }'
*/

/*
curl -X PUT http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "Description=%D0%A3%D1%87%D0%B5%D0%B1%D0%BD%D1%8B%D0%B5%20%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B8&Color=%2300FF00"
*/

/*
curl -X PUT http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "Description=Рабочие задачи&Color=#FFFFFF"
*/

/*
curl -X DELETE "http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b" \
     -H "Accept: application/json"
*/
