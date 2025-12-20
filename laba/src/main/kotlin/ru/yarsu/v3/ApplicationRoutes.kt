package ru.yarsu.v3

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.Category
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.jwt.Permissions
import ru.yarsu.requirePermission
import ru.yarsu.v3.handler.EisenhowerListHandler
import ru.yarsu.v3.handler.ListTimeHandler
import ru.yarsu.v3.handler.StatisticHandler
import ru.yarsu.v3.handler.AddNewTaskHandler
import ru.yarsu.v3.handler.CategoryHandler
import ru.yarsu.v3.handler.DeleteCategory
import ru.yarsu.v3.handler.DeleteUser
import ru.yarsu.v3.handler.PingHandler
import ru.yarsu.v3.handler.PutCategory
import ru.yarsu.v3.handler.TaskListHandler
import ru.yarsu.v3.handler.TaskShowHandler
import ru.yarsu.v3.handler.TaskShowPutHandler
import ru.yarsu.v3.handler.UsersHandler

fun applicationRoutes(
    taskList: MutableList<TaskModel>,
    categoriesList: MutableList<Category>,
    userList: MutableList<User>,
): RoutingHttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to PingHandler(),
            "/v3" bind
                routes(
                    "/tasks" bind Method.GET to TaskListHandler(taskList),
                    "/tasks" bind Method.POST to requirePermission(Permissions::createNewTask).then(AddNewTaskHandler(taskList, userList, categoriesList)),
                    "/tasks/eisenhower" bind Method.GET to requirePermission(Permissions::workWithTasksAndCategoryGet).then(EisenhowerListHandler(taskList)),
                    "/tasks/by-time" bind Method.GET to requirePermission(Permissions::workWithTasksAndCategoryGet).then(ListTimeHandler(taskList)),
                    "/tasks/statistics" bind Method.GET to requirePermission(Permissions::workWithTasksAndCategoryGet).then(StatisticHandler(taskList)),
                    "/tasks/{task-id}" bind Method.GET to requirePermission(Permissions::getInformationTask).then(TaskShowHandler(taskList, userList, categoriesList)),
                    "/tasks/{task-id}" bind Method.PUT to requirePermission(Permissions::editTask).then(TaskShowPutHandler(taskList, userList, categoriesList)),
                    "/categories" bind Method.GET to requirePermission(Permissions::workWithTasksAndCategoryGet).then(CategoryHandler(categoriesList, userList)),
                    "/categories/{category-id}" bind Method.PUT to requirePermission(Permissions::editAndDeleteCategory).then(PutCategory(categoriesList)),
                    "/categories/{category-id}" bind Method.DELETE to requirePermission(Permissions::editAndDeleteCategory).then(DeleteCategory(taskList, categoriesList)),
                    "/users" bind Method.GET to requirePermission(Permissions::getAllUsers).then(UsersHandler(userList)),
                    "/users/{user-id}" bind Method.DELETE to requirePermission(Permissions::deleteUser).then(DeleteUser(taskList, categoriesList, userList)),
                ),
        )
    return app
}

/*
curl -X POST http://localhost:9000/v2/tasks \
-H "Content-Type: application/json" \
-H ""Authorization: Bearer " \
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

curl -X PUT "http://localhost:9000/v2/tasks/8b2a4482-35c8-411e-aab9-6ce490b3bea1" \
     -H "Content-Type: application/json" \
     -d '{
           "Title": "Обновлённая задача",
           "Author": "de42a00f-7f43-4d10-808d-bee47fdeef49",
           "Category": "10c50d8a-fc9e-45ba-b303-7a623f4c699b"
         }'

curl -X PUT http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "Description=%D0%A3%D1%87%D0%B5%D0%B1%D0%BD%D1%8B%D0%B5%20%D0%B7%D0%B0%D0%B4%D0%B0%D1%87%D0%B8&Color=%2300FF00"

curl -X PUT http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "Description=Рабочие задачи&Color=#FFFFFF"

curl -X DELETE "http://localhost:9000/v2/categories/10c50d8a-fc9e-45ba-b303-7a623f4c699b" \
     -H "Accept: application/json"
*/
